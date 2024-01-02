import { BaseEntity, Prescription } from "../base-entity";
import { LEPActivity, SubstanceAdministration } from '.';

import { CodeValue, PQ } from '../data-types';

export interface Dosage extends BaseEntity {
  dayInterval?: number
  // dayOffset?: number // TODO add for VCO
  daymomentCode?: CodeValue
  daytime?: Date
  doseDie?: string
  duration?: string
  frequency?: CodeValue
  lepActivity?: LEPActivity
  prescription?: Prescription
  quantity?: PQ
  quantityTxt?: string
  substanceAdministration?: SubstanceAdministration
  type?: CodeValue
  weekDayCode?: CodeValue
}
