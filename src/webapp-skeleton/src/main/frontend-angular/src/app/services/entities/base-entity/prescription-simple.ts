import { TherapySimple } from '.';
import { CodeValuePhi } from '../data-types/code-value-phi';

export interface PrescriptionSimple {
  administration10?: boolean
  administration12?: boolean
  administration14?: boolean
  administration16?: boolean
  administration18?: boolean
  administration2?: boolean
  administration20?: boolean
  administration22?: boolean
  administration24?: boolean
  administration4?: boolean
  administration6?: boolean
  administration8?: boolean
  description?: String
  routeCode?: CodeValuePhi
  therapySimple?: TherapySimple
}
