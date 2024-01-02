import { BaseEntity } from './base-entity';
import { FavoriteSection } from './favorite-section';
import { ServiceDeliveryLocation } from './role';

import { CodeValue } from './data-types';
import { CodeValueProxy } from '../proxyes';

export interface FavoriteTab extends BaseEntity {
  numColumn?: number
  section?: Array<FavoriteSection>
  serviceDeliveryLocation?: ServiceDeliveryLocation
  sortOrder?: number
  subTypeCode?: CodeValue | CodeValueProxy // CodeValuePhi
  title?: string
  typeCode?: CodeValue | CodeValueProxy // CodeValuePhi
}
