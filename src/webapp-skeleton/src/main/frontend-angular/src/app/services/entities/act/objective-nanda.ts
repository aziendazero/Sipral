import { AuditedEntity } from '../auditedEntity/audited-entity';
import { LEPActivity, Nanda, PatientEncounter } from '.';
import { CodeValue, ED } from '../data-types';
import { CodeValueProxy } from '../../proxyes';

export interface ObjectiveNanda extends AuditedEntity {
  activity?: LEPActivity
  confirmed?: Boolean
  levelCode?: CodeValue | CodeValueProxy
  nanda?: Nanda
  note?: String
  objFrequency?: CodeValue
  objInitDate?: Date
  objLep?: CodeValue
  objLimitDate?: Date
  objReached?: CodeValue | CodeValueProxy // CodeValuePhi
  patientEncounter?: PatientEncounter
  text?: ED
  timeSpent?: number
}
