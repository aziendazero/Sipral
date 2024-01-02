import { ClinicalProcedure, ProcedureDefinition } from '.';
import { CodeValue, CodeValuePhi, PQ } from '../data-types';
import { CodeValueProxy } from '../../proxyes';

export interface AbstractProcedure extends ClinicalProcedure {
  approachSiteCode?: Set<CodeValue>
  charge?: CodeValuePhi
  classCode?: CodeValue
  code?: CodeValue | CodeValueProxy
  codeIcd9?: CodeValue
  description?: String
  fillerField1?: String
  fullPrice?: PQ
  hospitalPrice?: PQ
  levelCode?: CodeValuePhi
  mainProcedure?: Boolean
  methodCode?: Set<CodeValue>
  placerField1?: String
  placerField2?: String
  placerGroupNumber?: String
  priority?: CodeValue
  procedureDefinition?: ProcedureDefinition
  quantity?: number
  regionalCodeIcd9?: CodeValue
  regionPrice?: PQ
  resultHandling?: CodeValuePhi
  startDate?: Date
  targetSiteCode?: Set<CodeValue>
  text?: String
}
