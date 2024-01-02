import { BaseEntity } from '../base-entity';
import { Employee } from '../role/employee';

import { CodeValue } from '../data-types';
import { CodeValueProxy, EntityProxy } from '../../proxyes';

export interface AuditedEntity extends BaseEntity {
  author?: Employee
  authorRole?: CodeValue | CodeValueProxy // CodeValueRole
  availabilityTime?: Date
  cancellationDate?: Date
  cancellationNote?: string
  cancelledBy?: Employee
  cancelledByRole?: CodeValue | CodeValueProxy // CodeValueRole
}
