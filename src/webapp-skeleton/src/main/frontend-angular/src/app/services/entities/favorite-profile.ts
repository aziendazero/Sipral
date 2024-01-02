import { BaseEntity, Prescription } from './base-entity';
import { LEPActivity } from './act/lep-activity';
import { FavoriteSection } from './favorite-section';
import { EntityProxy } from '../proxyes/entity-proxy';

export interface FavoriteProfile extends BaseEntity {
  activity?: Array<LEPActivity>
  prescription?: Array<Prescription>
  section?: FavoriteSection | EntityProxy
  sortOrder?: number
  title?: string
}
