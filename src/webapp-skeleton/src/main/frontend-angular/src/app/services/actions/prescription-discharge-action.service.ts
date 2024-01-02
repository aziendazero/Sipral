import { Injectable, Injector } from '@angular/core';
import { BaseActionService } from './base-action.service';
import { PrescriptionDischarge } from '../entities/base-entity';
import { PrescriptionMedicineGeneric } from '../entities/base-entity/prescription-medicine-generic';
import { Dosage } from '../entities/act/dosage';
import { PrescriptionActionService } from './prescription-action.service';

@Injectable()
export class PrescriptionDischargeActionService extends BaseActionService<PrescriptionDischarge> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'PrescriptionDischarge';
  }

  public inject(id: number, additionals: Array<any> = null,
    loads: Array<any> = null, conversationName = null): Promise<PrescriptionDischarge> {
    return super.inject(id, additionals, loads, conversationName).then((prescription: PrescriptionDischarge) => {

      if (prescription) {

        // FIXME remove when widgets will create paths
        if (!prescription.quantity) {
          prescription.quantity = {};
        }
        if (prescription.prescriptionMedicine && prescription.prescriptionMedicine.length > 0) {
          prescription.prescriptionMedicine.map((pm: PrescriptionMedicineGeneric) => {
            if (!pm.dosage) {
              pm.dosage = [];
            }
          });
        }
        // END FIXME remove when widgets will create paths

        if (prescription.prescriptionMedicine
          && prescription.prescriptionMedicine.length >= 1 && prescription.prescriptionMedicine[0].dosage
          && prescription.prescriptionMedicine[0].dosage.length >= 1) {
          if (prescription.extemporaneous) {
            prescription.quantity = prescription.prescriptionMedicine[0].dosage[0].quantity;
          }
        }

        if (prescription && prescription.prescriptionMedicine && prescription.prescriptionMedicine.length > 0) {
          const dosage: Array<Dosage> = prescription.prescriptionMedicine[0].dosage;
          PrescriptionActionService.orderDosage(dosage);
        }
      }
      return prescription;
    });
  }

}
