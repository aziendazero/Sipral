import { BaseEntity, Appointment, AppointmentGrouper } from './';
import { Procedure } from '../act';
import { Employee } from '../role';
import { CodeValuePhi } from '../data-types';

export interface ProcedureRequest extends BaseEntity {
  appointment?: Appointment
  appointmentGrouper?: AppointmentGrouper
  author?: Employee
  codeExemption?: CodeValuePhi
  exemption?: CodeValuePhi
  flag?: boolean
  priority?: CodeValuePhi
  procedure?: Array<Procedure>
  requestNumber?: string
  requestType?: CodeValuePhi
  ssnCode?: CodeValuePhi
  text?: string
  urgency?: CodeValuePhi
}
