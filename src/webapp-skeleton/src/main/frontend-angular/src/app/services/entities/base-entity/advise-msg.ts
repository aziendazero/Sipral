import { BaseEntity } from '.';

import { Employee } from '../role';
import { CodeValue } from '../data-types';

export interface AdviseMsg extends BaseEntity {
  active?: boolean
  author?: Employee
  restrictTo?: Array<Employee>
  restrictToRol?: Array<CodeValue> // CodeValueRole
  scheduleFrom?: Date
  scheduleTo?: Date
  test?: boolean
  text?: string
}
