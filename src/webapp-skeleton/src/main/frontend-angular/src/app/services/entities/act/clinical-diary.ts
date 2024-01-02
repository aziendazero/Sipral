import { AuditedEntity } from '../auditedEntity/audited-entity';

import { PatientEncounter, LEPExecution } from '.';
import { ServiceDeliveryLocation } from '../role';

import { CodeValuePhi } from '../data-types';
import { CodeValueProxy, EntityProxy } from '../../proxyes';

export interface ClinicalDiary extends AuditedEntity {
  assignedSDL?: ServiceDeliveryLocation
  code?: CodeValuePhi | CodeValueProxy
  confirmed?: boolean
  execution?: LEPExecution
  expiry?: Date
  // internalActivity?: InternalActivity
  levelCode?: CodeValuePhi | CodeValueProxy
  observationDate?: Date
  patientEncounter?: PatientEncounter
  priority?: CodeValuePhi | CodeValueProxy
  // rehabProject?: RehabProject
  serviceDeliveryLocation?: ServiceDeliveryLocation | EntityProxy
  sourceCode?: CodeValuePhi | CodeValueProxy
  text?: string
  title?: string
}
