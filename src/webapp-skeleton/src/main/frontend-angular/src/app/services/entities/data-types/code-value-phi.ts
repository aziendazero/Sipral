import { CodeValue } from '.';

export interface CodeValuePhi extends CodeValue {
  abbreviation?: string
  enableAnnotation?: boolean
  inverseRelationsPhi?: Array<CodeValuePhi>
  // relationsCity?: Array<CodeValueCity> // TODO add
  relationsPhi?: Array<CodeValuePhi>
  score?: number
}
