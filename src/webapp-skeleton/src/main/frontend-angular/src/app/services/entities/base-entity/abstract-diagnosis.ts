import { AuditedEntity } from '../auditedEntity/audited-entity';
import { PatientEncounter } from '../act';

export interface AbstractDiagnosis extends AuditedEntity {
  patientEncounter?: PatientEncounter
}
