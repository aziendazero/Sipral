import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { Patient } from '../entities/role';
import { reviver } from '../converters/any.converter';
import { BannerActions } from '../../actions/banner.action';

@Injectable()
export class PatientActionService extends BaseActionService<Patient> {

  private static HISTORY = '/encounterHistory';
  private static ALLERGIES = '/allergies';

  constructor(
    injectorz: Injector,
    private BannerActions: BannerActions
  ) {
    super(injectorz);
    this.entityName = 'Patient';
  }

  public eject(): Promise<any> {
    return super.eject().then(() => this.BannerActions.refresh());
  }
  /**
   * public getEncounterHistory method
   * Read Encounter history
   */
  getEncounterHistory(patientInternalId: number, showHistoricEncounters: string, page = 0) {

    let parameters = '&';
    parameters += 'patientInternalId=' + patientInternalId;
    parameters += '&showHistoricEncounters=' + showHistoricEncounters;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + PatientActionService.HISTORY + '/' + page
      + ';jsessionid=' + this.sid + '?conversationId=' + this.cid + parameters,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
    )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        // this.entity = entity;
        // this.conversation['EncounterHistoryList'] = {entities: entity};
        this.conversationActions.put('EncounterHistoryList', {entities: entity});
      })
      .catch(error => {
        console.error('Error getEncounterHistory ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  countAllergies(patientInternalId: number): Promise<number> {

    let parameters = '&';
    parameters += 'patientInternalId=' + patientInternalId;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + PatientActionService.ALLERGIES
      + ';jsessionid=' + this.sid + '?conversationId=' + this.cid + parameters,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
    )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        if (entity[0] !== null) {
          return entity[0].allergy;
        } else {
          return 0;
        }
      })
      .catch(error => {
        console.error('Error countAllergies ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

}
