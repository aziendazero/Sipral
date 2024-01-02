import { AuditedEntity } from '../auditedEntity/audited-entity';
import { Patient, ServiceDeliveryLocation } from '../role';
import { PatientEncounter } from '.';
import { CodeValuePhi, ED } from '../data-types';

export interface Annotation extends AuditedEntity {
  assignedSDL?: ServiceDeliveryLocation
  code?: CodeValuePhi
  confirmed?: boolean
  isDeleted?: Boolean
  bannerNote?: Boolean
  levelCode?: CodeValuePhi
  note?: String
  observationDate?: Date
  patient?: Patient
  patientEncounter?: PatientEncounter
  text?: ED
  title?: ED
}
