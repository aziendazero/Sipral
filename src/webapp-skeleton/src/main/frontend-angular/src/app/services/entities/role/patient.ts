import { BaseEntity } from '../base-entity';
import { Employee, ServiceDeliveryLocation } from '.';
import { VitalSign } from '../act';
import { Organization } from '../entity';

import { AD, EN, CodeValue, II, TEL } from '../data-types';

export interface Patient extends BaseEntity {
  additionalId?: string
  addr?: AD
  birthPlace?: AD
  birthTime?: Date
  citizen?: CodeValue
  consent?: CodeValue; // KLINIK
  consentExpireDate?: Date; // KLINIK
  // TODO?: contact?: Array<Contact>
  countryOfBirth?: CodeValue // CodeValueCountry
  currentOrg?: Organization
  currentOrgCode?: CodeValue // CodeValuePhi
  date1?: Date
  date2?: Date
  date3?: Date
  deceasedTime?: Date
  district?: ServiceDeliveryLocation
  doc?: string
  doctor?: Employee
  domicileAddr?: AD
  educationLevelCode?: CodeValue
  externalConsent?: Boolean
  externalId?: string
  fiscalCode?: string
  foreignBirthPlace?: Boolean
  g2Anag?: string
  genderCode?: CodeValue // CodeValuePhi
  genericExemption?: string
  healthCardId?: string
  id?: Set<II>
  imported?: Boolean
  internalConsent?: Boolean
  jobTitle?: string
  language?: CodeValue // CodeValuePhi
  livingArrangementCode?: CodeValue
  maritalStatusCode?: CodeValue // CodeValuePhi
  name?: EN
  noAllergy?: Boolean
  note1?: string
  note2?: string
  note3?: string
  originalOrg?: Organization
  religiousAffiliationCode?: CodeValue // CodeValuePhi
  statusCode?: CodeValue // CodeValuePhi
  stp?: string
  teamId?: string
  telecom?: TEL
  vitalSign?: Array<VitalSign>
  notePolizza?: string
}
