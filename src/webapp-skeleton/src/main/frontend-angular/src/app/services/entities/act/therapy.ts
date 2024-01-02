import { BaseEntity, Prescription, PrescriptionDischarge } from '../base-entity';
import { PatientEncounter } from '.';
import { Employee } from '../role';

export interface Therapy extends BaseEntity {
  author?: Employee
  patientEncounter?: PatientEncounter
  prescription?: Array<Prescription>
  prescriptionDischarge?: Array<PrescriptionDischarge>
  // TODO: supplyRequest: Array<GenericRequest>
}
