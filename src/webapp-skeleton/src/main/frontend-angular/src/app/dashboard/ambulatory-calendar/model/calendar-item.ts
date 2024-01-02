import { CodeValuePhi } from './../../../services/entities/data-types/code-value-phi';
import { Appointment } from './../../../services/entities/base-entity/appointment';
import { CodeValue } from './../../../services/entities/data-types/code-value';
import { ServiceDeliveryLocation } from './../../../services/entities/role/service-delivery-location';
import { Patient, Employee } from '../../../services/entities/role';
import { Procedure, AgendaAnnotation } from '../../../services/entities/act';
import { AppointmentGrouper } from '../../../services/entities/base-entity';
export class CalendarItem {

    date: Date;
    duration: number;
    serviceDeliveryLocation: ServiceDeliveryLocation;
    note: string;
    statusCode: string;
    lengthCode: CodeValuePhi;
    status: CodeValue;
    insertCompleted: Boolean;
    isIndirect: Boolean;
    sourceCode?: CodeValuePhi;
    patient?: Patient;
    color: number;
    procedure?: Procedure;
    performedProcedure?: Array<Procedure>;
    appointmentGrouper?: AppointmentGrouper;
    internalId: number;
    encounterId: number;
    externalId?: string;
    visitType?: string;
    physiotherapist?: Employee;
    surgeon?: Employee;
    diagnosis?: string;
    anesthesia?: string;
    televisit?:Boolean;
    urlPath?: string;

    type: 'Appointment' | 'Note';

    public static newFromAppointment(a: Appointment): CalendarItem {
        return {
            date: a.defaultDate,
            duration: a.procedure ? a.procedure.levelCode.score : a.duration, // duration for KLINIK
            serviceDeliveryLocation: a.serviceDeliveryLocation,
            lengthCode: a.procedure ? a.procedure.levelCode : undefined,
            sourceCode: a.sourceCode ? a.sourceCode : undefined,
            note: a.text.string,
            statusCode: a.statusCode.code,
            status: a.statusCode,
            insertCompleted: a.insertCompleted,
            isIndirect: a.isIndirect,
            patient: a.patient,
            color: a.color,
            procedure: a.procedure,
            externalId:  a.externalId ? a.externalId : undefined,
            performedProcedure: a.performedProcedure, // KLINIK
            internalId: a.internalId,
            appointmentGrouper: a.appointmentGrouper,
            type: 'Appointment',
            encounterId: a.patientEncounter ? a.patientEncounter.internalId : null,
            televisit: a.televisit,
            urlPath: a.urlPath,

            visitType: a.visitType ? a.visitType.code : undefined,
            physiotherapist: a.physiotherapist ? a.physiotherapist : null, //KLINIK
            surgeon: a.surgeon ? a.surgeon : null,  //KLINIK
            diagnosis: a.diagnosis,
            anesthesia: a.anesthesia ? a.anesthesia.currentTranslation : null
        };
    }

    public static newFromAgenda(a: AgendaAnnotation): CalendarItem {
        return {
            date: a.availabilityTime,
            duration: a.lengthCode.score,
            lengthCode: a.lengthCode,
            serviceDeliveryLocation: a.serviceDeliveryLocation,
            note: a.text.string,
            color: a.color,
            isIndirect: null,
            insertCompleted: null,
            procedure: null,
            performedProcedure: null,
            status: null,
            statusCode: null,
            patient: null,
            internalId: a.internalId,
            appointmentGrouper: null,
            type: 'Note',
            encounterId: null
        };
    }
}
