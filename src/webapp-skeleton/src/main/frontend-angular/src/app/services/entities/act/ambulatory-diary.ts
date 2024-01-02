import { Annotation } from './annotation';
import { AppointmentGrouper, Classification } from '../base-entity';
import { ServiceDeliveryLocation } from '../role';

export interface AmbulatoryDiary extends Annotation {
  appointmentGrouper?: AppointmentGrouper
  classification?: Classification
  evidence?: String
  isPrivate?: Boolean
  noteAmb?: String
  serviceDeliveryLocation?: ServiceDeliveryLocation
}
