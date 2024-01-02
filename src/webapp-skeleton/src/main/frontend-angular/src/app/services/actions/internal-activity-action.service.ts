import { Injectable, Injector } from '@angular/core';
import { BaseActionService } from './base-action.service';
import { InternalActivity } from '../entities/base-entity';
import { reviver } from '../converters/any.converter';
import { Datamodel } from '../datamodel/datamodel';


@Injectable()
export class InternalActivityActionService extends BaseActionService<InternalActivity> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'InternalActivity';
  }

  public getInternalActivities(patientId: number, sdlId): Promise<Datamodel> {
    return this.httpService.fetch(
      this.apiUrl + this.restBaseUrl + this.entityUrl + '/patients/' + patientId + '/' + sdlId,
      {
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        let dm: Datamodel = new Datamodel(raw || [], this.httpService);
        return dm;
      });
  }

  public updateSdl(appointmentId: number): Promise<InternalActivity> {
    return this.httpService.fetch(
      this.apiUrl + this.restBaseUrl + this.entityUrl + '/updateSdl?appointmentId=' + appointmentId,
      {
        method: 'PUT',
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return raw;
      });
  }

  public copyInternalActivities(appointmentId: number, internalActivityId: number): Promise<any> {

    let body = 'appointmentId=' + appointmentId + '&internalActivityId=' + internalActivityId;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/appointments',
      {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        credentials: 'include',
        method: 'POST',
        body: body,
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return raw;
      });
  }

}
