import { BaseEntity } from '../base-entity';
import { Medicine } from './index';

import { AD, EN } from '../data-types';

export interface Manufacturer extends BaseEntity {
  addr: AD
  externalId: string
  medicine : Array<Medicine>
  name: EN
  vat: string
}
