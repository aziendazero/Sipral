import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { reviver } from '../../../services/converters/any.converter';
import { environment } from '../../../../environments/environment';
import { DictionaryManager } from 'app/services';

@Injectable()
export class AdtAction extends DashboardBaseAction {

  protected static STATUS = '/status';

  constructor(@Inject('apiUrl') protected apiUrl,
    protected conversationActions: ConversationActions,
    protected dictionaryManager: DictionaryManager,
    protected router: Router,
    protected httpService: HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'PatientEncounter';
    this.dashboardUrl = environment.adt.action;
  }

  public refresh(): Promise<any> {

    if (this.equal['assigned'] === false) {
      delete this.equal['assigned'];
    }

    if (this.equal['temporary'] === false) {
      delete this.equal['temporary'];
    }

    if (this.equal['surgicalVisFlag'] === false) {
      delete this.equal['surgicalVisFlag'];
    }

    return super.refresh().then((dm: Datamodel) => {
      if (dm) {
        if (dm.entities) {
          dm.entities.map(e => {
            // Klinik query fixes to be similar to vco
            if (e.patient.name instanceof Object) { // Klinik query returns fam and giv, at vco name contains all
              e.patient.name.toString = () => `${e.patient.name.fam} ${e.patient.name.giv}`;
            }
            if (e.statuscode instanceof Object) { // Klinik query returns statuscode.code, vco returns statuscode
              e.statuscode = e.statuscode.code;
            }
            if (e.sdl) { // Klinik returns sdl, Vco returns location
              e.location = { name: e.sdl.name.giv };
            }
          });
        }

        // If action has a sorting set sorting to Datamodel to show sortingArrow;
        if (this.equal['sorting']) {
          if (this.equal['sorting'].indexOf('DESC') !== -1) {
            dm._orderDirection = 'DESC';
          } else {
            dm._orderDirection = 'ASC';
          }
          if (this.equal['sorting'].indexOf('SurnameName') !== -1) {
            dm._sortingColumn = 'patient.name';
          } else if (this.equal['sorting'].indexOf('AdmissionDate') !== -1) {
            dm._sortingColumn = 'availabilitytime';
          } else if (this.equal['sorting'].indexOf('WardRoomBed') !== -1) {
            dm._sortingColumn = 'support.name';
          }
        }

        dm.orderByColumn = (sortingColumn, trueOrdering) => {

          let trueSorting = 'SurnameName';
          if (sortingColumn === 'patient.name') {
            trueSorting = 'SurnameName';
          } else if (sortingColumn === 'availabilitytime') {
            trueSorting = 'AdmissionDate';
          } else if (sortingColumn === 'support.name') {
            trueSorting = 'WardRoomBed';
          }

          // Save current sorting into action service to mantain that order also after returning to adt dashboard
          this.equal['sorting'] = trueSorting + trueOrdering;

          this.refresh();
        }
      }
    });
  }

  public getAlarmDetails(nosologic: string, tipo: string): Promise<Datamodel> {
    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.dashboardUrl + '/getAlarmDetails' +
      '/nosologic=' + nosologic, { credentials: 'include' })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver))
      .then(raw => {

        if (raw && raw.Esito) {
          this.conversationActions.remove('TherapyAlarmList');
        } else {

          if (raw && raw.DATA && raw.DATA.DETTAGLIO_ALLARMI_PAZIENTE) {
            let results: Array<any> = raw.DATA.DETTAGLIO_ALLARMI_PAZIENTE.map((row) => {
              let date = null;
              if (row.DataPianificata) {
                date = new Date(row.DataPianificata);
              }
              return {

                descrizione: row.Descrizione,
                ambito: row.Ambito,
                note: row.Note,
                dataPianificata: date,
                allarme: row.Allarme,
                modulo: row.Modulo
              };

            });
            return new Datamodel(results.filter(function (alarm) {
              return alarm.ambito === tipo;
            }) || []);
          }
        }
      });
  }

  /**
   * Change status
   */
  changeStatus(encounterId: number, appointmentId: number, action: string, cancellationNote: string): Promise<any> {

    return this.httpService.fetch(this.buildUrl(this.apiUrl + this.restBaseUrl + this.dashboardUrl + AdtAction.STATUS),
      {
        method: 'POST',
        body: 'appointmentId=' + appointmentId + '&action=' + action + '&encounterId=' + encounterId +
          '&cancellationNote=' + cancellationNote,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver))
      .then(text => {
        this.refresh();
        return text;
      })
      .catch(error => {
        console.error('Error changeStatus ' + encounterId + ' ' + error.message);
        throw error;
      });
  }

}
