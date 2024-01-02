import { BaseEntity } from '../base-entity/base-entity';
import { ServiceDeliveryLocation } from '../role/service-delivery-location';
import { CodeValue, CodeValuePhi, PQ } from '../data-types';

export interface ProcedureDefinition extends BaseEntity {
  anatomicOrder?: number
  brancaCode?: Array<CodeValue>
  category?: CodeValuePhi
  classCode?: CodeValue
  code?: CodeValue
  codeIcd9?: CodeValue
  color?: number
  current?: boolean
  daysBeforeOperation?: number
  daysGap?: number
  daysValidity?: number
  defaultLength?: CodeValuePhi
  endValidity?: Date
  exclusiveProcedure?: boolean
  frequency?: number
  fullPrice?: PQ
  hospitalPrice?: PQ
  procedureSDL?: Array<ServiceDeliveryLocation>
  quantity?: number
  regionalCodeIcd9?: CodeValue
  regionPrice?: PQ
  serviceDeliveryLocation?: ServiceDeliveryLocation
  startValidity?: Date
  subCategory?: CodeValuePhi
  text?: string
  textDe?: string
  textEn?: string
  vatCode?: CodeValuePhi
}
