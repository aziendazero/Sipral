import { BaseEntity } from '.';
import { PatientEncounter } from '../act';

export interface PrimaVitalSign extends BaseEntity {
  alertLevel?: number
  alertLevelChangeDate?: Date
  insertedVitalSigns?: string
  lastInsertDate?: Date
  monitoringFrequency?: number
  patientEncounter?: PatientEncounter
}
