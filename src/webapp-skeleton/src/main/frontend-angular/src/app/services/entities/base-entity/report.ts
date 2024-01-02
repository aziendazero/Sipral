import { AuditedEntity } from '../auditedEntity/audited-entity';
import { Appointment, AppointmentGrouper } from '.';
import { ServiceDeliveryLocation, Employee, Patient } from '../role';
import { PatientEncounter } from '../act';
import { CodeValuePhi } from '../data-types';

export interface Report extends AuditedEntity {
  appointment?: Appointment
  appointmentGrouper?: AppointmentGrouper
  availableToPatient?: boolean
  code?: CodeValuePhi
  confirmed?: boolean
  disOption?: CodeValuePhi
  district?: ServiceDeliveryLocation
  fseObscuration?: boolean
  isPrivate?: boolean
  isUpdate?: boolean
  lastAuthor?: Employee
  patient?: Patient
  patientEncounter?: PatientEncounter
  privateData?: boolean
  respDate?: Date
  serviceDeliveryLocation?: ServiceDeliveryLocation
  stateMode?: string
}
