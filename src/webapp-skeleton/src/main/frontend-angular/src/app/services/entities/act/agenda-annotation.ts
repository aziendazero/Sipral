import { CodeValuePhi } from './../data-types/code-value-phi';
import { Annotation } from '.';
import { ServiceDeliveryLocation } from '../role';

export interface AgendaAnnotation extends Annotation {
  serviceDeliveryLocation?: ServiceDeliveryLocation,
  color?: number,
  lengthCode?: CodeValuePhi
}
