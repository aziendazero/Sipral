import { ClinicalProcedure } from './clinical-procedure';
import { LEPActivity } from '.';
import { CodeValue, IVL } from '../data-types';
import { CodeValueProxy } from '../../proxyes';


export interface LEPExecution extends ClinicalProcedure {
  executionDate?: IVL<Date>
  executionTime?: number
  lepActivity?: LEPActivity
  note?: String
  plannedDate?: Date
  plannedTime?: number
  statusDetailsCode?: CodeValue | CodeValueProxy
}
