import { Component, EventEmitter, Injector, OnDestroy, Output } from '@angular/core';
import { Location } from '@angular/common';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { PatientActionService } from '../../../services/actions/patient-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { ViewManager } from '../../../services/view-manager';
import { AppointmentGrouper } from '../../../services/entities/base-entity/appointment-grouper';
import { AppointmentGrouperActionService } from '../../../services/actions/appointment-grouper-action.service';
import { ActivatedRoute } from '@angular/router';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ServiceDeliveryLocationActionService } from '../../../services/actions/service-delivery-location-action.service';
import { Subscription } from 'rxjs';
import { DataService } from 'app/services/data.service';

@Component({
selector: 'phi-patient-search',
templateUrl: './patient-search.html'
})
export class PatientSearch extends BaseForm implements OnDestroy {
  @select(['conversation', 'PatientList']) PatientList$;
  patientListSub;
  PatientList;

  Patient;

  @Output() select : EventEmitter<any> = new EventEmitter();

  surname: string;
  name: string;

  birthTimeFrom: Date;
  birthTimeTo: Date;

  sdlId: string;
  
  private ignoreDeadPatients: boolean = false;
  private subscriptions: Subscription;

  constructor(injector: Injector,
              private location: Location,
              route: ActivatedRoute,
              public PatientAction: PatientActionService,
              public AppointmentGrouperAction: AppointmentGrouperActionService,
              public ServiceDeliveryLocationAction: ServiceDeliveryLocationActionService,
              private conversationActions: ConversationActions, 
              private viewManager: ViewManager,
              private dataService: DataService
              ) {
    super(injector);

    this.subscriptions = new Subscription();
    this.subscriptions.add(this.dataService.currentIgnoreDeadPatientsOption$.subscribe(o => this.ignoreDeadPatients = o));
    this.sdlId = route.snapshot.params['sdlId'];

    if (this.sdlId !== 'all') {
      this.ServiceDeliveryLocationAction.inject(+this.sdlId);
    }

    this.patientListSub = this.PatientList$.subscribe(res => this.PatientList = res);

    this.PatientAction.cleanRestrictions();
    if(this.ignoreDeadPatients === true){
      this.PatientAction.isNull['deceasedTime'] = 'true';
    }
    this.PatientAction.orderBy['name.fam'] = 'ascending';
    this.PatientAction.orderBy['name.giv'] = 'ascending';
    this.PatientAction.orderBy['birthTime'] = 'ascending';

    this.AppointmentGrouperAction.cleanRestrictions();
    this.AppointmentGrouperAction.equal['serviceDeliveryLocation.internalId'] = this.sdlId;
    this.AppointmentGrouperAction.equal['statusCode.code'] = ['planned', 'active'];
    this.AppointmentGrouperAction.orderBy['patient.name.fam'] = 'ascending';
    this.AppointmentGrouperAction.orderBy['patient.name.giv'] = 'ascending';
    this.AppointmentGrouperAction.orderBy['patient.birthTime'] = 'ascending';
  }
  
  ngOnDestroy() {
    this.patientListSub.unsubscribe();
    this.conversationActions.remove('PatientList');
    this.subscriptions.unsubscribe();
  }

  surnameChanged(value) {
    if (value) {
      this.PatientAction.like['name.fam'] = value;
      this.AppointmentGrouperAction.like['patient.name.fam'] = value;
    } else {
      delete this.PatientAction.like['name.fam'];
      delete this.AppointmentGrouperAction.like['patient.name.fam'];
    }
  }

  nameChanged(value) {
    if (value) {
      this.PatientAction.like['name.giv'] = value;
      this.AppointmentGrouperAction.like['patient.name.giv'] = value;
    } else {
      delete this.PatientAction.like['name.giv'];
      delete this.AppointmentGrouperAction.like['patient.name.giv'];
    }
  }

  birthTimeFromChanged(value) {
    if (value) {
      this.PatientAction.greaterEqual['birthTime'] = value;
      this.AppointmentGrouperAction.greaterEqual['patient.birthTime'] = value;
    } else {
      delete this.PatientAction.greaterEqual['birthTime'];
      delete this.AppointmentGrouperAction.greaterEqual['patient.birthTime'];
    }
  }

  birthTimeToChanged(value) {
    if (value) {
      this.PatientAction.lessEqual['birthTime'] = value;
      this.AppointmentGrouperAction.lessEqual['patient.birthTime'] = value;
    } else {
      delete this.PatientAction.lessEqual['birthTime'];
      delete this.AppointmentGrouperAction.lessEqual['patient.birthTime'];
    }
  }

  onSearch() {
    if (this.sdlId === 'all') {
      this.PatientAction.read();
    } else {
      this.AppointmentGrouperAction.read().then((dm: Datamodel) => {
        let patientDm: Datamodel;
        if (dm && dm.entities) {
          patientDm = new Datamodel(dm.entities.map((appGrp: AppointmentGrouper) => {
            if (appGrp.patient.entityName) { // proxy json
              const agWithUnproxiedPat: AppointmentGrouper = dm.entities.find((currAg: AppointmentGrouper) =>
                currAg.patient.internalId === appGrp.patient.internalId
              );
              if (agWithUnproxiedPat) {
                appGrp.patient = agWithUnproxiedPat.patient;
              } else {
                throw new Error('Unable to find Patient into json ' + appGrp.patient.internalId );
              }
            }
            appGrp.patient['appointmentGrouper'] = appGrp; // add inverse relation
            return appGrp.patient;
          }));
        } else {
          patientDm = new Datamodel([]);
        }
        this.conversationActions.put('PatientList', patientDm);
      });
    }
    this.ignoreDeadPatients === true && this.dataService.setIgnoreDeadPatientsOption(false);
  }

  ie(entity, conversationName): Promise<any> {
    // Not injecting patient because Patient injection ejects Appointment
    // return this.PatientAction.inject(entity.internalId);
    this.Patient = entity;

    if (entity.appointmentGrouper) {
      return this.AppointmentGrouperAction.inject(entity.appointmentGrouper.internalId, null, ['appointment']).then(() => {
        delete entity.appointmentGrouper;
        return entity
      });
    } else {
      return Promise.resolve(entity);
    }

  }

  onSelect() {
    this.select.emit(this.Patient);
    this.location.back();
  }

  onCancel() {
    this.location.back();
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }

}
