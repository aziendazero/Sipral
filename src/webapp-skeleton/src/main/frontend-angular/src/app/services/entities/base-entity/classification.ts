import { BaseEntity } from '.';
import { ServiceDeliveryLocation } from '../role';
import { CodeValuePhi } from '../data-types';


export interface Classification extends BaseEntity {
  classificationCodes?: Array<CodeValuePhi>
  languageCode?: CodeValuePhi
  serviceDeliveryLocation?: ServiceDeliveryLocation
  text?: string
  title?: string
}
