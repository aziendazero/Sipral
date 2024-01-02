import { BaseEntity, AppointmentGrouper, ProcedureRequest } from '.';
import { Patient, Employee, ServiceDeliveryLocation } from '../role';
import { PatientEncounter, Procedure } from '../act';
import { CodeValue, CodeValuePhi, ED } from '../data-types';

export interface Appointment extends BaseEntity {
  activationDate?: Date
  appointmentGrouper?: AppointmentGrouper
  author?: Employee
  cancReasonCode?: CodeValue
  cancReasonDetail?: string
  color?: number
  completionDate?: Date
  confirmDate?: Date
  createdByLocation?: string
  defaultDate?: Date
  duration?: number
  externalId?: string
  externalIdRoot?: string
  externalReference?: string
  firstAppointment?: Boolean
  groupers?: Array<AppointmentGrouper>
  indirectProcedure?: Boolean
  insertCompleted?: Boolean
  isIndirect?: Boolean
  modificationDate?: Date
  modifiedBy?: string
  modifiedByLocation?: string
  nurseVisiblity?: Boolean
  patient?: Patient
  physiotherapist?: Employee
  surgeon?: Employee
  patientEncounter?: PatientEncounter
  performedProcedure?: Array<Procedure>
  procedure?: Procedure
  procedureRequest?: Array<ProcedureRequest>
  serviceDeliveryLocation?: ServiceDeliveryLocation
  sourceCode?: CodeValuePhi
  statusCode?: CodeValue
  stepMarker?: string
  text?: ED
  visitType?: CodeValuePhi
  workingDiagnosis?: string
  diagnosis?: string;
  anesthesia?: CodeValuePhi;
  televisit?: Boolean;
  urlPath?: string;
}
