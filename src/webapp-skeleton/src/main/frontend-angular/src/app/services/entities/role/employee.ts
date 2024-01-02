import { BaseEntity, EmployeeRole } from "../base-entity";
import { Organization } from "../entity";

import { AD, CodeValue, ED, EN, II, IVL, TEL } from "../data-types";

export interface Employee extends BaseEntity {
  username?: string;
  additionalId?: string;
  addr?: AD;
  ageTypeCode?: Array<CodeValue>;
  alias?: string;
  birthPlace?: AD;
  birthTime?: Date;
  code?: CodeValue;
  defaultLanguageCode?: CodeValue; // CodeValuePhi
  effectiveTime?: IVL<Date>;
  employeeRole?: Array<EmployeeRole>;
  externalId?: string;
  fiscalCode?: string;
  genderCode?: CodeValue; // CodeValuePhi
  hazardExposureText?: ED;
  host?: string;
  id?: Set<II>;
  isNew?: Boolean;
  jobClassCode?: CodeValue;
  jobCode?: CodeValue;
  lastAccessDate?: Date;
  lastChangedPassword?: Date;
  locationCode?: CodeValue;
  locationsCode?: Array<CodeValue>;
  loginCount?: number;
  name?: EN;
  note?: string;
  occupationCode?: CodeValue;
  organization?: Organization;
  password?: string;
  protectiveEquipmentText?: ED;
  salaryTypeCode?: CodeValue;
  sessionId?: string;
  structure?: string;
  student?: Boolean;
  tci?: string;
  telecom?: TEL;
  title?: string;
  upg?: Boolean;
}
