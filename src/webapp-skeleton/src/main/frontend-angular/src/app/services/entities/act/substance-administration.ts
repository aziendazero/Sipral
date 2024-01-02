import { Dosage, ClinicalProcedure } from '.';
import { Medicine, Prescription } from '../base-entity';
import { Employee } from '../role';

import { CodeValue, ED, IVL, PQ } from '../data-types';
import { CodeValueProxy, EntityProxy } from '../../proxyes';

export interface SubstanceAdministration extends ClinicalProcedure {
  administratedDate: IVL<Date>
  administratedQuantity: PQ
  administrationUnitCode: CodeValue
  dosage: Array<Dosage>
  doseQuantity: IVL<PQ>
  medicine: Array<Medicine>
  plannedDate: Date
  plannedQuantity: PQ
  prescription: Prescription
  rateQuantity: IVL<PQ>
  routeCode: CodeValue
  statusDetailsCode: CodeValue
  stopAuthor: Employee | EntityProxy
  stopAuthorRole: CodeValue | CodeValueProxy // CodeValueRole
  text: ED
}
