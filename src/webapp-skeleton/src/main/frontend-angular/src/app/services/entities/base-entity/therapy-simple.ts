import { BaseEntity, PrescriptionSimple } from '.';
import { PatientEncounter } from '../act';
import { Employee } from '../role';

export interface TherapySimple extends BaseEntity {
  author?: Employee
  editDate?: Date
  note?: String
  patientEncounter?: PatientEncounter
  prescriptionSimple?: Array<PrescriptionSimple>
}
