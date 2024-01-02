import { BaseEntity } from '../base-entity';
import { Employee } from '.';
import { Organization } from '../entity';

import { AD, CodeValue, EN, II, IVL, TEL } from '../data-types';

export interface ServiceDeliveryLocation extends BaseEntity {
  addr?: AD
  admissionCode?: Array<CodeValue>
  ageType?: Array<CodeValue> // Array<CodeValuePhi>
  area?: CodeValue // CodeValuePhi
  branch?: CodeValue // CodeValuePhi
  children?: Array<ServiceDeliveryLocation>
  classCode?: CodeValue
  // TODO?: classification?: Array<Classification>
  clinicDay?: CodeValue // CodeValuePhi
  clinicPeriod?: CodeValue // CodeValuePhi
  clinicSite?: String
  clinicType?: CodeValue // CodeValuePhi
  code?: CodeValue
  color?: number
  confidentialityCode?: Array<CodeValue>
  defaultPainType?: CodeValue // CodeValuePhi
  defaultVitalSign?: CodeValue // CodeValuePhi
  discipline?: CodeValue // CodeValuePhi
  effectiveTime?: IVL<Date>
  encounterCode?: Array<CodeValue>
  examinationType?: Array<CodeValue>
  externalCostCenter?: Array<CodeValue>
  footer?: String
  frequency?: CodeValue // CodeValuePhi
  g2Strt0?: String
  header?: String
  hybridManagementSupported?: Boolean
  id?: Set<II>
  inpsExternalCallAllowed?: Boolean
  intActSupported?: Boolean
  internalCostCenter?: Array<CodeValue>
  isPrivacyDefault?: Boolean
  ArrayDocType?: Array<CodeValue> // Array<CodeValuePhi>
  ArrayScalesType?: Array<CodeValue> // Array<CodeValuePhi>
  locationsCode?: Array<CodeValue> // Array<CodeValuePhi>
  logo?: any[] // FIXME?: byte[]
  mandatoryClassification?: Boolean
  name?: EN
  // TODO?: nodeInfo?: NestedSetNodeInfo<ServiceDeliveryLocation>
  oncoCertConf?: Array<CodeValue> // Array<CodeValuePhi>
  ordering?: number
  organization?: Organization
  overBooking?: Boolean
  parent?: ServiceDeliveryLocation
  priorityCode?: Array<CodeValue>
  procTimeFrom?: String
  psychoOncology?: Boolean
  reasonCode?: Array<CodeValue>
  rehabProc?: boolean
  reportTypeCombo?: CodeValue
  responsible?: Employee
  serviceDeliveryLocation?: ServiceDeliveryLocation
  slotDuration?: number
  specialization?: CodeValue // CodeValuePhi
  subtype?: CodeValue // CodeValuePhi
  telecom?: TEL
  timeCloseAt?: number
  timeOpenFrom?: number
  vitalSignForm?: CodeValue // CodeValuePhi
  waitingListSupported?: Boolean
  nurseVisibility?: Boolean
  timeBands?: Array<BaseEntity>
}
