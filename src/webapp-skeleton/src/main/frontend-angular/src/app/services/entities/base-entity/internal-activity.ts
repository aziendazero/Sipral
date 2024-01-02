import { Appointment, AppointmentGrouper } from '.';
import { PatientEncounter, ClinicalDiary } from '../act';
import { Employee, Patient, ServiceDeliveryLocation } from '../role';
import { AuditedEntity } from '../auditedEntity/audited-entity';
import { CodeValuePhi } from '../data-types/code-value-phi';

export interface InternalActivity extends AuditedEntity {
  activityType?: CodeValuePhi
  ageType?: CodeValuePhi
  anticoagTherapy?: CodeValuePhi
  appointment?: Appointment
  appointmentGrouper?: AppointmentGrouper
  bedridden?: boolean
  cancDate?: Date
  cancelNote?: string
  clinicalDiary?: Array<ClinicalDiary>
  code?: CodeValuePhi
  completedBy?: Employee
  completedDate?: Date
  completedNote?: string
  confirmed?: boolean
  costCenter?: CodeValuePhi
  createdByLocation?: string
  details?: string
  employee?: Employee
  endDate?: Date
  examination?: boolean
  examType?: CodeValuePhi
  examTypeArray?: Array<CodeValuePhi>
  fromSdl?: ServiceDeliveryLocation
  intActServices?: string
  location?: CodeValuePhi
  modificationDate?: Date
  modifiedBy?: string
  modifiedByLocation?: string
  note?: string
  nullifiedBy?: Employee
  nullifiedDate?: Date
  nullifiedNote?: string
  patient?: Patient
  patientEncounter?: PatientEncounter
  priority?: CodeValuePhi
  receiverNote?: string
  requestDate?: Date
  requesterNote?: string
  responsable?: string
  serviceDeliveryLocation?: ServiceDeliveryLocation
  statusCode?: CodeValuePhi
  toSdl?: ServiceDeliveryLocation
  fallRisk?: boolean
}
