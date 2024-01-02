import { CodeValue, CodeValuePhi } from '.';

export interface CodeValueCodifa extends CodeValue {
  relationsPhi?: Array<CodeValuePhi>
}
