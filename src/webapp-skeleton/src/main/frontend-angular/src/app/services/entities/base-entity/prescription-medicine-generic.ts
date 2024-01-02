import { AuditedEntity } from '../auditedEntity/audited-entity';
import { Medicine, PrescriptionGeneric } from '.';
import { Dosage } from '../act';

export interface PrescriptionMedicineGeneric extends AuditedEntity {
  dosage?: Array<Dosage>
  dosageSerialized?: String
  medicine?: Medicine
  prescription?: PrescriptionGeneric
  templateDosageKey?: String
}
