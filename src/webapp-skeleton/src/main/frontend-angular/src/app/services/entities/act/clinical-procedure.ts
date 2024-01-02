import { AuditedEntity } from '../auditedEntity/audited-entity';

import { CodeValue } from '../data-types';
import { CodeValueProxy } from '../../proxyes';

export interface ClinicalProcedure extends AuditedEntity {
  statusCode?: CodeValue | CodeValueProxy
}
