import { BaseEntity } from './base-entity';
import { Dosage } from './act';
import { ServiceDeliveryLocation } from './role';

import { CodeValuePhi } from './data-types';
import { EntityProxy, CodeValueProxy } from '../proxyes';

export interface TemplateDosage extends BaseEntity {
  code?: CodeValuePhi | CodeValueProxy
  dosage?: Array<Dosage>
  key?: string
  serviceDeliveryLocation?: ServiceDeliveryLocation | EntityProxy
  title?: string
}
