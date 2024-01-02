import { Inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { CodeValue } from '../../../services/entities/data-types/code-value';
import { environment } from '../../../../environments/environment';
import { SelectItem } from 'app/services/datamodel/select-item';
import { DictionaryManager } from 'app/services';

@Injectable()
export class AmbulatoryPortalActionService extends DashboardBaseAction {

  private static STATUS: string = '/status';

  private usedModalities = null;

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService: HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);

    this.dashboardName = 'AmbulatoryPortal';
    this.dashboardUrl = 'ambulatoryportal';
  }

  public refreshBy(name: string, surname: string, sdLocs: Array<any>, selectedDate: Date, selectedDateEnd: Date,
    selectedSentBy: Array<CodeValue>, selectedStatuses: Array<CodeValue>, selectedModality, selectedEmployee: SelectItem): Promise<Datamodel> {

    this.cleanRestrictions();

    this.equal['patient_name_giv'] = name;
    this.equal['patient_name_fam'] = surname;
    this.equal['agenda'] = sdLocs.map((cv) => cv.id);
    this.equal['location'] = sdLocs.filter((cv) => cv.type === 'DIS').map((cv) => cv.id);
    this.equal['endDate'] = environment.ambulatoryPortal.dateRange ? selectedDateEnd : selectedDate;
    this.equal['code.code'] = ['AMB', 'EMER'];
    this.equal['startDate'] = selectedDate;
    this.equal['sourceCode.code'] = selectedSentBy.map((cv) => cv.code); // = ['OWN', 'SSN'];
    this.equal['statusCode.id'] = selectedStatuses;
    this.equal['modality'] = selectedModality ? selectedModality.id : null;
    this.equal['authorId'] = selectedEmployee ? selectedEmployee.value : null;

    return super.initOrRefresh().then((dm: Datamodel) => {

      const ambulatoryAppointmentList = new Datamodel([]);
      const ambulatoryAppointmentInChagreList = new Datamodel([]);

      if (dm) {
        dm.entities.map(row => {

          if (row.statuscode.code === 'new') {
            ambulatoryAppointmentList.entities.push(row);
          } else {
            ambulatoryAppointmentInChagreList.entities.push(row);
          }
        });
      }

      this.conversationActions.put('AmbulatoryAppointmentList', ambulatoryAppointmentList);
      this.conversationActions.put('AmbulatoryAppointmentInChargeList', ambulatoryAppointmentInChagreList);

      return dm;
    });
  }

  changeStatus(action: string, encounterId: number, reasonCancCodeId: string = null,
    cancellationNote: string = null, appointmentId: number = 0): Promise<any> {

    // Terrible format for retrocompatibility Flex dashboard
    let formData = 'action=' + action +
      '&encounterId=' + encounterId +
      '&appointmentId=' + appointmentId;
    if (reasonCancCodeId) {
      formData = formData + '&reasonCancCodeId=' + reasonCancCodeId;
    }
    if (cancellationNote) {
      formData = formData + '&cancellationNote=' + cancellationNote;
    }

    return this.httpService.fetch(this.buildUrl(this.apiUrl + this.restBaseUrl + this.dashboardUrl + AmbulatoryPortalActionService.STATUS),
      {
        method: 'POST',
        body: formData,
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded'}),
        credentials: 'include'
      })
      .catch(error => {
        console.error('Error changeStatus rest method ' + action + ' ' + encounterId + ' ' + error.message);
        throw error;
      });
  }

}
