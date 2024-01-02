import { BaseEntity, Appointment } from "../base-entity";
import { Nanda, Therapy, VitalSign } from '.';
import { Employee, Patient, ServiceDeliveryLocation } from '../role';
import { CodeValue, ED, II, PQ } from '../data-types';

export interface PatientEncounter extends BaseEntity {
  accessCode?: string
  acuityLevelCode?: CodeValue
  admissionReferralSourceCode?: CodeValue
  admitter?: Employee
  // TODO?: anamnesis?: Array<MedicalHistory>
  appointment?: Array<Appointment>
  arrivalModalityCode?: CodeValue
  // TODO?: assessmentScale?: Array<AssessmentScale>
  assignedSDL?: ServiceDeliveryLocation
  assignment?: Date
  attender?: Employee
  availabilityTime?: Date
  barcode?: string
  bed?: ServiceDeliveryLocation
  bedstring?: string
  bradenScore?: number
  brassScore?: number
  morseScore?:number
  callCode?: string
  classeASA?: CodeValue // CodeValuePhi
  clinicalSuspect?: string
  code?: CodeValue
  colorCode?: CodeValue
  complexity?: string
  convocationDate?: Date
  createdByLocation?: string
  deambulationCode?: CodeValue
  details?: string
  // TODO?: diagnosis?: Array<Diagnosis>
  dischargeDate?: Date
  dischargeDispositionCode?: CodeValue
  // TODO?: dischargeProcedure?: Array<Procedure>
  discharger?: Employee
  dismissionDate?: Date
  emergencyEncounterId?: II // IIEmbeddable
  exemptionCode?: CodeValue // CodeValueExemption
  externalCause?: CodeValue // CodeValuePhi
  fallRisk?: boolean
  g2Aasdo?: string
  g2CodSdo?: string
  g2Epis?: string
  g2Epsd?: string
  g2Rico?: string
  id?: Set<II>
  intervention?: CodeValue // CodeValueIcd9
  isConfidentialCode?: CodeValue
  lastChecking?: Date
  lastClinicalDiary?: Date
  lengthOfStayQuantity?: PQ
  mainProblemCode?: CodeValue
  missionCode?: string
  modificationDate?: Date
  modifiedBy?: string
  modifiedByLocation?: string
  nanda?: Array<Nanda>
  nortonScore?: number
  numeroImpegnativa?: string
  nurseReportCode?: CodeValue // CodeValuePhi
  // TODO?: objectiveExam?: Array<ObjectiveExam>
  onereDegenza?: CodeValue // CodeValuePhi
  operationCenterCode?: string
  optionTPN?: Boolean
  originCode?: CodeValue // CodeValuePhi
  PACCode?: CodeValue // CodeValueIcd9
  pain?: number // Double
  patient?: Patient
  patientClass?: CodeValue
  paymentAgreementCode?: CodeValue // CodeValuePhi
  plannedProcedures?: Array<CodeValue> // Array<CodeValueIcd9>
  preadmitNumber?: II // IIEmbeddable
  preAdmitTestInd?: Boolean
  priorityCode?: CodeValue // CodeValuePhi
  // TODO?: procedure?: Array<EncounterProcedure>
  reasonCode?: CodeValue // CodeValuePhi
  referrer?: Employee
  responsabileInvio?: CodeValue // CodeValuePhi
  roomstring?: string
  scheduledSDL?: ServiceDeliveryLocation
  serviceDeliveryLocation?: ServiceDeliveryLocation
  specialArrangementCode?: Set<CodeValue>
  specialCourtesiesCode?: Set<CodeValue>
  specialityCode?: CodeValue // CodeValuePhi
  statementId?: string
  statusCode?: CodeValue
  suggested?: Boolean
  surgeryDrainName?: string
  suspectedDiagnosis?: CodeValue // CodeValueIcd9
  temporarySDL?: ServiceDeliveryLocation
  text?: ED
  therapy?: Array<Therapy>
  tpCode?: CodeValue
  transferInstituteCode?: string
  traumaTypeCode?: CodeValue
  visitNumber?: II // IIEmbeddable
  vitalSign?: Array<VitalSign>
  invoicingClosed?: boolean
  invoicingClosedDate?: Date
  invoicingClosedBy?: Employee
  sdoClosed?: boolean
  sdoClosedDate?: Date
  sdoClosedBy?: Employee
}
