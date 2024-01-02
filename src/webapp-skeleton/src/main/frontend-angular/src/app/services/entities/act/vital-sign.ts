import { AuditedEntity } from '../auditedEntity/audited-entity';

import { PatientEncounter } from ".";
import { Patient, ServiceDeliveryLocation } from '../role';

import { PQ, PQD, CodeValuePhi } from '../data-types';

export interface VitalSign extends AuditedEntity {
  serialVersionUID?: number
  administration1?: PQD
  administration2?: PQD
  administration3?: PQD
  administration4?: PQD
  administration5?: PQD
  administration6?: PQD
  // appointmentGrouper?: AppointmentGrouper
  bodyTemperature?: PQD
  breathFrequency?: PQD
  cardiacFrequency?: PQD
  cc?: PQ
  ccPerc?: PQ
  code?: CodeValuePhi
  confirmed?: boolean
  cvc?: Boolean
  cvcDate?: Date
  cvp?: PQ
  diameterEyeL?: PQ
  diameterEyeR?: PQ
  diastolic?: PQD
  diastolicAnkDx?: PQD
  diastolicAnkSx?: PQD
  diastolicArmSx?: PQD
  diuresis?: PQD
  drain?: PQ
  drainChest?: PQ
  drainLowerLeft?: PQ
  drainLowerRight?: PQ
  drainUpperLeft?: PQ
  drainUpperRight?: PQ
  ega?: Boolean
  egaDate?: Date
  etCO2?: PQD
  evaluationDate?: Date
  gastricProbe?: Boolean
  gastricProbeDate?: Date
  gcs?: PQ
  glycemia?: PQ
  height?: PQ
  heightPerc?: PQ
  hematicLoss?: PQD
  ilestomy?: PQ
  insertMode?: String
  // intraoperatoryCard?: IntraoperatoryCard
  invasiveDiastolic?: PQD
  invasiveSystolic?: PQD
  measureOrIO1?: PQD
  measureOrIO2?: PQD
  measureOrIO3?: PQD
  measureOrIO4?: PQD
  measureOrIO5?: PQD
  measureOrIO6?: PQD
  morePresInfo?: Boolean
  o2Saturation?: PQD
  pain?: PQ
  patient?: Patient
  patientEncounter?: PatientEncounter
  perspiratio?: PQD
  reactionEyeL?: CodeValuePhi
  reactionEyeR?: CodeValuePhi
  rectalTemperature?: PQ
  serviceDeliveryLocation?: ServiceDeliveryLocation
  sng?: PQ
  systolic?: PQD
  systolicAnkDx?: PQD
  systolicAnkSx?: PQD
  systolicArmSx?: PQD
  tv?: PQD
  urineStick?: PQ
  weight?: PQ
  weightPerc?: PQ
}
