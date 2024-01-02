import { AuditedEntity } from '../auditedEntity/audited-entity';
import { PrescriptionMedicineGeneric } from '.';
import { Therapy } from '../act';

import { CodeValue, PQ } from '../data-types';
import { CodeValueProxy, EntityProxy } from '../../proxyes';

export interface PrescriptionGeneric extends AuditedEntity {
  code?: CodeValue | CodeValueProxy
  continuous?: boolean
  deleteNote?: string
  extemporaneous?: boolean
  glySecondaryProtocol?: boolean
  hoursGap?: string
  infusionDuration?: PQ
  infusionSpeed?: PQ
  infusionTypeCode?: CodeValue | CodeValueProxy
  maximumQuantity24h?: PQ
  minsGap?: string
  modified?: Boolean
  needsBased?: boolean
  note?: string
  other?: string
  painTypeCode?: CodeValue | CodeValueProxy
  prescriptionMedicine?: Array<PrescriptionMedicineGeneric>
  quantity?: PQ
  rootPrescription?: PrescriptionGeneric | EntityProxy
  routeCode?: CodeValue | CodeValueProxy
  scheduling?: string
  statusCode?: CodeValue | CodeValueProxy
  therapy?: Therapy | EntityProxy
  urgent?: boolean
}
