import { Injectable, Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { BaseActionService } from './base-action.service';
import { VitalSign } from '../entities/act';
import { Datamodel } from '../datamodel/datamodel';
import { PQ } from '../entities/data-types/pq';

@Injectable()
export class VitalSignActionService extends BaseActionService<VitalSign> {

  constructor(injectorz: Injector,
              private translateService: TranslateService) {
    super(injectorz);
    this.entityName = 'VitalSign';
    this.entityUrl = 'vitalsigns';
  }


  getLastVitalSign(encounterId: number): Promise<Array<any>> {

    this.select.push('evaluationDate');
    //PA
    this.select.push('systolic.value');
    this.select.push('diastolic.value');
    //SPO2
    this.select.push('o2Saturation.value');
    //FC
    this.select.push('cardiacFrequency.value');
    //T
    this.select.push('bodyTemperature.value');
    //GLC
    this.select.push('glycemia.value');
    // FIXME Diuresi
    this.select.push('urineStick.value');
    //Dolore
    this.select.push('pain.value');
    //Peso
    this.select.push('weight.value');
    //Altezza
    // this.select.push('height.value');


    this.equal['patientEncounter.internalId'] = encounterId;
    this.equal['isActive'] = true;
    this.equal['confirmed'] = true;

    this.orderBy['evaluationDate'] = 'descending';

    return this.read().then((dm: Datamodel) => {
      let results: any = {};

      if(dm.entities != null && dm.entities.length > 0) {

        // let vs: VitalSign = dm.entities[0];
        dm.entities.map((vs: VitalSign) => {

          this.addVs(results, 'Systolic_pressure', vs.systolic, vs.evaluationDate);
          this.addVs(results, 'Diastolic_pressure', vs.diastolic, vs.evaluationDate);
          this.addVs(results, 'Temperature', vs.bodyTemperature, vs.evaluationDate);
          this.addVs(results, 'Diuresis', vs.urineStick, vs.evaluationDate);
          this.addVs(results, 'Glycemia', vs.glycemia, vs.evaluationDate);
          this.addVs(results, 'Hearth_rate', vs.cardiacFrequency, vs.evaluationDate);
          this.addVs(results, 'SPO2', vs.o2Saturation, vs.evaluationDate);
          this.addVs(results, 'Pain', vs.pain, vs.evaluationDate);
          this.addVs(results, 'Weight', vs.weight, vs.evaluationDate);

        });
      }

      // Same as ES2017 : Object.values(results)
      const res: Array<any> = Object.keys(results).map(key=>results[key]);

      this.conversationActions.put('VitalSignList', new Datamodel(res));
      return res;

    });
  }

  private addVs(results: any, name: string, qty: PQ, date: Date) {
    if (!results.hasOwnProperty(name) || !results[name].value) {
      let row: any = {};
      row.name = this.translateService.instant(name);
      if (qty != null && qty.value) {
        row.value = qty.value;
        row.date = date;
      }
      results[name] = row;
    }
  }

}
