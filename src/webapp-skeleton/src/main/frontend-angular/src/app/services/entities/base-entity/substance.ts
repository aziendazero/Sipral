import { BaseEntity } from '../base-entity';
import { Medicine } from '.';

import { EN } from '../data-types';

export interface Substance extends BaseEntity {
  externalId: string
  medicine: Array<Medicine>
  name: EN
}
