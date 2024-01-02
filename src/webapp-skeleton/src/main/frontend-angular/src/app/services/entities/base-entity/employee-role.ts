import { BaseEntity } from '../base-entity';
import { Employee, ServiceDeliveryLocation } from '../role';

import { CodeValue, IVL } from '../data-types';

export interface EmployeeRole extends BaseEntity {
  ageTypeCode: Array<CodeValue> // Array<CodeValuePhi>
  // TODO: application: Array<Application>
  code: CodeValue // CodeValueRole
  effectiveTime: IVL<Date>
  employee: Employee
  enabledOffices: Array<CodeValue> // Array<CodeValuePhi>
  enabledServiceDeliveryLocations: Array<ServiceDeliveryLocation>
  favoriteSdl: String
  isCoordinator: Boolean
  locationsCode: Array<CodeValue> // Array<CodeValuePhi>
  managInstit: number // Long
  managInstitName: String
  specialization: CodeValue // CodeValuePhi
  defaultRole: Boolean
  comment: String
  loginComment: String
}
