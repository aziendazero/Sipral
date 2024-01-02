import { AuditedEntity } from '../auditedEntity/audited-entity';
import { Dosage, LEPExecution, Nanda, ObjectiveNanda, PatientEncounter } from '.';
import { FavoriteProfile } from '../favorite-profile';
import { CodeValue, IVL } from '../data-types';
import { CodeValueProxy } from '../../proxyes';

export interface LEPActivity extends AuditedEntity {
  confirmed?: boolean
  dosage?: Array<Dosage>
  effectiveDate?: IVL<Date>
  extemporaneous?: boolean
  lepExecution?: Array<LEPExecution>
  lepFrequency?: CodeValue | CodeValueProxy // CodeValuePhi
  lepSource?: CodeValue | CodeValueProxy // CodeValuePhi
  modified?: boolean
  nanda?: Nanda
  nandaLep?: CodeValue // CodeValueNanda
  note?: string
  objective?: ObjectiveNanda
  patientEncounter?: PatientEncounter
  profile?: FavoriteProfile
  responsibleRole?: CodeValue | CodeValueProxy // CodeValueRole
  statusCode?: CodeValue | CodeValueProxy // CodeValuePhi
  supportNumber?: number
  supportRole?: CodeValue | CodeValueProxy // CodeValueRole
  timeSpent?: number
  validityPeriod?: IVL<Date>
  // TODO vitalSign?: VitalSign
}
