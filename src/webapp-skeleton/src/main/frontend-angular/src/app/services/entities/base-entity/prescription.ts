import { PrescriptionGeneric } from '.';
import { SubstanceAdministration } from '../act';
import { FavoriteProfile } from '../favorite-profile';

import { CodeValue, IVL, PQ } from '../data-types';

export interface Prescription extends PrescriptionGeneric {
  diastolicPressure?: PQ
  diastolicPressureThreshold?: CodeValue
  diuresis?: PQ
  diuresisThreshold?: CodeValue
  glycemia?: PQ
  glycemiaThreshold?: CodeValue
  heartRate?: PQ
  heartRateThreshold?: CodeValue
  pain?: PQ
  painThreshold?: CodeValue
  period?: IVL<Date>
  profile?: FavoriteProfile
  spo2?: PQ
  spo2Threshold?: CodeValue
  substanceAdministration?: Array<SubstanceAdministration>
  systolicPressure?: PQ
  systolicPressureThreshold?: CodeValue
  temperature?: PQ
  temperatureThreshold?: CodeValue
  validityPeriod?: IVL<Date>
}
