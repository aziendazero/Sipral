import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { PatientEncounter } from '../entities/act';
import { ActionError } from '../error/action-error';


@Injectable()
export class PatientEncounterActionService extends BaseActionService<PatientEncounter> {

  private static UPDATE_LAST_CLINICAL_DIARY: string = '/updateLastClinicalDiary';

  public static ACTION_COMPLETE: string = 'complete';
  public static ACTION_ACTIVE: string = 'active';
  public static ACTION_CANCEL: string = 'cancelled';
  public static ACTION_DISCHARGE: string = 'discharged';

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'PatientEncounter';
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null): Promise<any> {
    return super.inject(id, additionals, loads);
  }


  public getServiceDeliveryLocationId(patientEncounter): number {

    if (patientEncounter) {
      let sdlInternalId = null;

      if ((this.employeeRoleCode === '10')
        || (this.employeeRoleCode === '12')
        || (this.employeeRoleCode === '21')) {
        if (patientEncounter.temporarySDL) {
          sdlInternalId = patientEncounter.temporarySDL.internalId;
        } else {
          if (patientEncounter.assignedSDL) {
            sdlInternalId = patientEncounter.assignedSDL.internalId;
          }
        }
      } else {
        if (patientEncounter.assignedSDL) {
          sdlInternalId = patientEncounter.assignedSDL.internalId;
        }
      }

      if (sdlInternalId) {
        return sdlInternalId;
      } else {
        throw new ActionError('Patient Encounter without ServiceDeliveryLocation', patientEncounter);
      }
    } else {
      //return Promise.resolve(null);
      return null;
    }
  }

  public updateLastClinicalDiary(encounterId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + PatientEncounterActionService.UPDATE_LAST_CLINICAL_DIARY
        + '/' + encounterId + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'POST',
        // body: 'action=' + action,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        credentials: 'include'
      })
      .then(response => {
        return {type: PatientEncounterActionService.UPDATE_LAST_CLINICAL_DIARY, receivedAt: Date.now()};
      })
      .catch(error => {
        console.error('Error updateLastClinicalDiary ' + encounterId + ' ' + error.message);
        throw error;
      });
  }

}
