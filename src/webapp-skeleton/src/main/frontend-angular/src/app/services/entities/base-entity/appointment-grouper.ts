import { BaseEntity } from '../base-entity';
import { Appointment } from '.';
import { Patient, Employee, ServiceDeliveryLocation } from '../role';
import { VitalSign } from '../act';
import { CodeValue, CodeValuePhi, ED} from '../data-types';

export interface AppointmentGrouper extends BaseEntity {
  ageType?: CodeValue
  appointment?: Array<Appointment>
  author?: Employee
  availabilityTime?: Date
  availableFrom?: Date
  availableSDLoc?: Array<ServiceDeliveryLocation>
  cancDate?: Date
  cancelledBy?: Employee
  cancelNote?: string
  code?: CodeValue
  completionDate?: Date
  derivationExpr?: string
  diagnosis?: CodeValue
  diagnosisIcd9?: CodeValue
  disabilityCode?: CodeValuePhi
  disabilityPriority?: Boolean
  hasCallAnnotation?: boolean
  languageCode?: CodeValue
  levelCode?: CodeValue
  location?: CodeValue
  // FIXME medicalHistory?: Array<MedicalHistory>
  otherFirstLanguage?: string
  otherSecondLanguage?: string
  patient?: Patient
  patientType?: CodeValue
  phoneNumber?: string
  priority?: CodeValue
  // FIXME procedureRequests?: Array<ProcedureRequest>
  relyOn?: CodeValuePhi
  // FIXME report?: Array<Report>
  rootAppointment?: Appointment
  school?: string
  secondaryDiagnosis?: CodeValue
  secondLanguage?: CodeValue
  sentBy?: CodeValue
  serviceDeliveryLocation?: ServiceDeliveryLocation
  socialServ?: CodeValuePhi
  spokenLanguage?: CodeValue
  statusCode?: CodeValue
  statusFam?: CodeValuePhi
  text?: ED
  therapyType?: CodeValue
  title?: ED
  uncertaintyCode?: CodeValue
  unit?: CodeValuePhi
  vitalSign?: Array<VitalSign>
}
