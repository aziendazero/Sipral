import { AbstractProcedure, PatientEncounter } from '.';
import { ProcedureRequest } from '../base-entity';
import { ServiceDeliveryLocation } from '../role';


export interface Procedure extends AbstractProcedure {
  billed?: boolean
  current?: boolean
  diagnostics?: string
  executeDate?: Date
  patientEncounter?: PatientEncounter
  placerOrderNumber?: string
  placerOrderNumberRoot?: string
  procedureRequest?: ProcedureRequest
  // FIXME: add   report?: AmbulatoryReport
  requestDate?: Date
  serviceDeliveryLocation?: ServiceDeliveryLocation
  textDe?: String
  textEn?: String
}
