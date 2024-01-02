import { BaseEntity } from './base-entity';
import { FavoriteProfile } from './favorite-profile';
import { FavoriteTab } from './favorite-tab';
import { EntityProxy } from '../proxyes/entity-proxy';

export interface FavoriteSection extends BaseEntity {
  color?: string
  columnIndex?: number
  profile?: Array<FavoriteProfile>
  sortOrder?: number
  tab?: FavoriteTab | EntityProxy
  title?: string
}
