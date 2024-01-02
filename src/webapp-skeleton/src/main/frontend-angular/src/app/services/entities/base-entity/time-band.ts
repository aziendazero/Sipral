import { BaseEntity } from '../base-entity';
import { ServiceDeliveryLocation } from '../role';

export let timeBandDays: Array<string> = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];

export interface TimeBand extends BaseEntity {
  color: string;
  startTime: Date;
  endTime: Date;
  startDate: Date;
  endDate: Date;
  monday: boolean;
  tuesday: boolean;
  wednesday: boolean;
  thursday: boolean;
  friday: boolean;
  saturday: boolean;
  sunday: boolean;
  serviceDeliveryLocation: ServiceDeliveryLocation;
}
