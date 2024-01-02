import { Report } from '.';
import { Procedure } from '../act';

export interface AmbulatoryReport extends Report {
  // diagnosis : Array<Diagnosis> // FIXME add
  procedure : Array<Procedure>
}
