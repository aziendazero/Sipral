import { AbstractDiagnosis } from '../base-entity';
import { LEPActivity } from '.';
import { CodeValue } from '../data-types';
import { CodeValueProxy } from '../../proxyes';

export interface Nanda extends AbstractDiagnosis {
  activity?: Array<LEPActivity>
  actNandaDate?: Date
  confirmed?: Boolean
  consequence?: string
  consequenceCode?: Array<CodeValue>
  consequenceDiag?: CodeValue | CodeValueProxy // CodeValueNanda
  diagType?: Boolean
  elseNandaBM?: string
  elseNandaRF?: string
  nandaBFelse?: string
  nandaBFSign?: Array<CodeValue> // CodeValueNanda
  nandaBFstr?: string
  nandaBM?: Array<CodeValue> // CodeValueNanda
  nandaDiag?: CodeValue // CodeValueNanda
  nandaRF?: Array<CodeValue> // CodeValueNanda
  objective?: Array<CodeValue> // CodeValueNanda
  progNumber?: number
  resources?: string
  riskCode?: CodeValue | CodeValueProxy
  riskType?: Boolean
  statusCode?: CodeValue | CodeValueProxy
  titleDiag?: string
  typePCP?: CodeValue | CodeValueProxy
}
