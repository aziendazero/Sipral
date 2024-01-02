
import { CalendarItem } from './../../dashboard/ambulatory-calendar/model/calendar-item';
import { ServiceDeliveryLocation } from './../entities/role/service-delivery-location';


import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseActionService } from './base-action.service';
import { AgendaAnnotation } from '../entities/act/agenda-annotation';

import { anyToString, reviver } from '../converters/any.converter';

@Injectable()
export class AgendaAnnotationActionService extends BaseActionService<AgendaAnnotation> {



  @select(['config', 'sdLocs']) sdLocs$;
  sdLocs: Array<any>;
  employee: any;
  employeeRoleCode: any;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'AgendaAnnotation';

    this.sdLocs$.subscribe((sdLocs) => {
      this.sdLocs = sdLocs;
    });
  }

  public newAgendaAnnotation(sdlId: number, date: Date): AgendaAnnotation {
    this.entity = {};

    this.entity.availabilityTime = date;

    const sdl = this.sdLocs.find((sdl) => sdl.id === sdlId.toString());

    this.entity.serviceDeliveryLocation = {
      internalId: sdlId,
      entityName: 'com.phi.entities.role.ServiceDeliveryLocation',
      name: { giv: sdl.text }
    };

    this.entity.text = {};

    this.conversationActions.put(this._entityName, this.entity);

    return this.entity;
  }
  private static COPY = '/copy';
  /**
   * Read AgendaAnnotations and assciate to ServiceDeliveryLocations
   * @param {Date} from
   * @param {Array<ServiceDeliveryLocation>} sdlocs
   * @returns {Promise<any>}
   */
  public readByDateAndAgenda(from: Date, to: Date, agendaIds: Array<number>): Promise<any> {

    this.cleanRestrictions();
    this.readPageSize = 0;
    this.filterBySdl = false;
    this.select.push('availabilityTime');
    this.select.push('lengthCode.score');
    this.select.push('serviceDeliveryLocation');
    this.select.push('text.string');
    this.select.push('color');
    this.greaterEqual['availabilityTime'] = from;
    this.lessEqual['availabilityTime'] = to;
    this.equal['serviceDeliveryLocation.internalId'] = agendaIds;

    return this.read();
  }


  public copyAgendaAnnotation(calendarItem: CalendarItem, date: Date, sdlId: number) {
    let clone: AgendaAnnotation = {};
    if (calendarItem.duration) {

      clone.lengthCode = Object.assign({}, calendarItem.lengthCode);
    }



    if (calendarItem.color) {
      clone.color = calendarItem.color;
    }
    if (calendarItem.note) {
      if (!clone.text) {
        clone.text = {};
      }

      clone.text.string = calendarItem.note;
    }
    if (date) {
      clone.availabilityTime = date;
    }
    if (sdlId) {
      clone.serviceDeliveryLocation = {
        internalId: sdlId,
        entityName: 'com.phi.entities.role.ServiceDeliveryLocation'
      };
    }
    this.entity = clone;
  }

  public copyAgendaAnnotationFromAgenda(agendaAnnotation: AgendaAnnotation, date: Date, sdlId: number) {
    let clone: AgendaAnnotation = {};
    if (agendaAnnotation.lengthCode) {

      clone.lengthCode = Object.assign({}, agendaAnnotation.lengthCode);
    }



    if (agendaAnnotation.color) {
      clone.color = agendaAnnotation.color;
    }
    if (agendaAnnotation.text.string) {
      if (!clone.text) {
        clone.text = {};
      }
      clone.text.string = agendaAnnotation.text.string;
    }
    if (date) {
      clone.availabilityTime = date;
    }
    if (sdlId) {
      clone.serviceDeliveryLocation = {
        internalId: sdlId,
        entityName: 'com.phi.entities.role.ServiceDeliveryLocation'
      };
    }
    this.entity = clone;
  }

}

