import { Injectable, Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { dateToTime } from '../converters/date.converter';
import { BaseActionService } from './base-action.service';
import { TemplateDosage } from '../entities/template-dosage';
import { SelectItem } from '../datamodel/select-item';
import { Dosage } from '../entities/act/dosage';

@Injectable()
export class TemplateDosageActionService extends BaseActionService<TemplateDosage> {

  public static personalized: SelectItem;
  public static asNeeded: SelectItem;
  public static extemporaneous: SelectItem;
  public static urgent: SelectItem;
  public static continuous: SelectItem;

  translateService: TranslateService;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'TemplateDosage';
    this.translateService = injectorz.get(TranslateService);

    TemplateDosageActionService.personalized = {value: 'personalized', label: this.translateService.instant('personalized')};
    TemplateDosageActionService.asNeeded = {value: 'as-needed', label: this.translateService.instant('as-needed')};
    TemplateDosageActionService.extemporaneous = {value: 'extemporaneous', label: this.translateService.instant('extemporaneous')};
    TemplateDosageActionService.urgent = {value: 'urgent', label: this.translateService.instant('urgent')};
    TemplateDosageActionService.continuous = {value: 'continuous', label: this.translateService.instant('continuous')};
  }

  public newTemplateDosage(serviceDeliveryLocationId: number, code: 'THERAPY' | 'ACTIVITY'): TemplateDosage {
    this.entity = {};
    this.entity.code = { codeSystemName: 'PHIDIC', domainName: 'FavoriteType', code: code };
    this.entity.dosage = [];
    this.entity.serviceDeliveryLocation = { internalId: serviceDeliveryLocationId, entityName: 'com.phi.entities.role.ServiceDeliveryLocation' };
    this.conversationActions.put(this._entityName, this.entity);
    return this.entity;
  }

    // Read TemplateDosages
    public readTemplateDosages(currentSdlId, code = 'THERAPY') {
      this.cleanRestrictions();
  
      this.equal['isActive'] = true;
      this.equal['serviceDeliveryLocation.internalId'] = currentSdlId;
      this.equal['code.code'] = code;
      this.orderBy['title'] = 'ascending';
      this.read();
    }

  public getAllTemplateDosages(): Array<SelectItem> {
    if (this.list) {
      let results: Array<SelectItem> = this.list.map((td:TemplateDosage) => {
        return {value: td.internalId, label: td.title}
      });
      return results;
    }
    return [];
  }

  public getDosages4Activity(): Array<SelectItem> {
    let results:Array<SelectItem> = this.getAllTemplateDosages();

    if (results) {
      results.splice(0, 0, TemplateDosageActionService.personalized);
    }
    return results;
  }

  public getDosages4Prescription(prescriptionCode: string, isProfile: boolean, dischargeMode: boolean): Array<SelectItem> {

    let results:Array<SelectItem> = this.getAllTemplateDosages();

    if (dischargeMode) {
      results.splice(0, 0, TemplateDosageActionService.personalized);
      results.splice(1, 0, TemplateDosageActionService.asNeeded);
    } else {
      if (prescriptionCode === 'PHARMA') {
        results.splice(0, 0, TemplateDosageActionService.personalized);
        results.splice(1, 0, TemplateDosageActionService.asNeeded);
        results.splice(2, 0, TemplateDosageActionService.extemporaneous);
        if (!isProfile) {
          results.splice(3, 0, TemplateDosageActionService.urgent);
        }
      } else if (prescriptionCode === 'INFU') {
        results.splice(0, 0, TemplateDosageActionService.personalized);
        results.splice(1, 0, TemplateDosageActionService.asNeeded);
        results.splice(2, 0, TemplateDosageActionService.continuous);
      }
    }
  return results;

  }

  /**
   * Find a TemplateDosage into results of last read and return a clone of dosages
   * @param templateDosageId
   * @param copyQuantity
   * @returns {Array<Dosage>}
     */
  public findAndClone(templateDosageId: number, copyQuantity: boolean): Array<Dosage> {
    let doaseges: Array<Dosage> = [];
    if (this.list) {
      let td: TemplateDosage = this.list.find((td: TemplateDosage) => td.internalId === templateDosageId);
      if (td) {
        doaseges = td.dosage.map((td: Dosage) => {
          let d: Dosage = {};
          d.dayInterval = td.dayInterval;
          d.daytime = td.daytime;
          if (copyQuantity) {
            d.quantity = td.quantity || { value: 1 };
          } else {
            d.quantity = { value: 1 };
          }
          return d;
        });
      }
    }
    return doaseges;
  }


  /**
   * Get templateDosage id or personalized
   * @param dosage
   * @param sdl
   * @returns {string}
   */
  public getTemplateDosageId4Therapy(dosage: Array<Dosage>, serviceDeliveryLocationId: number): string | number {
    return this.getTemplateDosageId(dosage, serviceDeliveryLocationId, 'T');
  }

  public getTemplateDosageId4Activity(dosage: Array<Dosage>, serviceDeliveryLocationId: number): string | number {
    return this.getTemplateDosageId(dosage, serviceDeliveryLocationId, 'A');
  }

  private getTemplateDosageId(dosage: Array<Dosage>, serviceDeliveryLocationId: number, separator = 'T'): string | number {

    var key = '';

    if (dosage != null && dosage.length > 0) {
      key = serviceDeliveryLocationId + ';' + separator + '=';

      for (let dos of dosage) {
        if (dos.dayInterval && dos.daytime) {
          key += dos.dayInterval + "@" + dateToTime(dos.daytime) + "|";
        }
      }

      key = key.substring(0, key.length - 1);
    }

    let temlateDosageId: string | number;

    if (this.list) {
      let temlateDosage: TemplateDosage = this.list.find((td:TemplateDosage) => td.key === key);
      if (temlateDosage) {
        temlateDosageId = temlateDosage.internalId;
      }
    }
    if (!temlateDosageId && dosage.length > 0) {
      temlateDosageId = TemplateDosageActionService.personalized.value;
    }
    return temlateDosageId;
  }

}
