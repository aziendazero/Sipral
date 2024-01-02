import { BaseEntity } from '../base-entity';

import { AD, CodeValue, EN, TEL } from '../data-types';

export interface Organization extends BaseEntity {
  addr: AD
  headerText: string
  id: string
  name: EN
  standardIndustryClassCode: CodeValue
  telecom: TEL
  validFrom: Date
  validTo: Date
  vatNumber: string
}
