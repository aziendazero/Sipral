import { Injectable, Injector } from '@angular/core';
import { BaseActionService } from './base-action.service';
import { AmbulatoryDiary } from '../entities/act';
import { reviver } from '../converters/any.converter';
import { Datamodel } from '../datamodel/datamodel';

@Injectable()
export class AmbulatoryDiaryActionService extends BaseActionService<AmbulatoryDiary> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'AmbulatoryDiary';
  }

  getByPatient(patientId: number, sdlId: number): Promise<Array<any>> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/byPatient/' + patientId + '/' + sdlId
      + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
    )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver ))
      .then(list => {
        this.conversationActions.put(this.entityName + 'List', new Datamodel(list));
        return list;
      });
  }

}
