import { Component, Injector, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { CarePlanAction } from './actions/care-plan-action.service';
import { PatientEncounterActionService, NandaActionService, ObjectiveNandaActionService,
  LEPActivityActionService, LEPExecutionActionService, ClinicalDiaryActionService, TemplateDosageActionService } from '../../services/actions';
import { PatientEncounter, Nanda, ObjectiveNanda, LEPActivity, LEPExecution } from '../../services/entities/act';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ViewManager } from '../../services/view-manager';
import { ConversationActions } from '../../actions/conversation.actions';
import { Diagnosis } from './components/diagnosis';
import { DiagnosisDelete } from './components/diagnosis-delete';
import { LepActivityDelete } from './components/lep-activity-delete';
import { Objective } from './components/objective';
import { BaseForm } from '../../widgets/form/base-form';
import { LepActivityEdit } from '../favorite/activity/lep-activity-edit';
import { ObjectiveClose } from './components/objective-close';
import { environment } from '../../../environments/environment';

@Component({
  templateUrl: './activity-prescriber.html',
  styleUrls: ['./activity-prescriber.scss']
})
export class ActivityPrescriber extends BaseForm implements OnInit {

  @select(['conversation']) conversation$;

  CarePlanList: Datamodel;
  PatientEncounter: PatientEncounter;

  environment = environment;

  constructor(injector: Injector,
              private router: Router,
              private viewManager: ViewManager,
              private PatientEncounterAction: PatientEncounterActionService,
              public CarePlanAction: CarePlanAction,
              private TemplateDosageAction: TemplateDosageActionService,
              private NandaAction: NandaActionService,
              private ObjectiveNandaAction: ObjectiveNandaActionService,
              private LEPActivityAction: LEPActivityActionService,
              private LEPExecutionAction: LEPExecutionActionService,
              private ClinicalDiaryAction: ClinicalDiaryActionService,
              private conversationActions: ConversationActions,
              private translateService: TranslateService) {

    super(injector);

    this.conversation$.subscribe(res => {
      this.CarePlanList = res.CarePlanList;
      this.PatientEncounter = res.PatientEncounter;
    });

  }

  ngOnInit() {
    if (this.PatientEncounter && this.PatientEncounter.code.code === 'IMP') {
      this.refresh();
    } else {
      this.router.navigate(['/dashboard', 'adt']);
    }
  }

  refresh(type = 'carePlan') {
    if (type === 'carePlan') {
      this.CarePlanAction.equal['refreshType'] = 'carePlan';
    } else {
      this.CarePlanAction.equal['refreshType'] = 'activities';
    }
    this.CarePlanAction.equal['encounterId'] = this.PatientEncounter.internalId;
    this.CarePlanAction.refresh();

    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    if(sdlId){
      this.TemplateDosageAction.readTemplateDosages(sdlId, 'ACTIVITY');
    }
  }

  reportCarePlan() {
    this.CarePlanAction.printReport('reports/r_carePlan.seam');
  }

  onTypeChange(event) {
    this.refresh(event === 0 ? 'carePlan' : 'activities');
  }

  addCodedDiag() {
    this.addDiagonsis(true);
  }

  addNotCodedDiag() {
    this.addDiagonsis(false);
  }

  addDiagonsis(coded = false) {
    this.CarePlanAction.getDiagnosesCount(this.PatientEncounter.internalId).then((diagTot: number) => {
      this.NandaAction.newNanda(this.PatientEncounter, diagTot, coded);

      this.viewManager.setPopupViewId('diagnosis').then((diagnosis: Diagnosis) => {
        diagnosis.create.subscribe(() => {
          this.NandaAction.create().then(() => {
            this.viewManager.setPopupViewId(null);
            this.refresh();
          });
        });
      });
    });
  }

  delete(CarePlan) {
    this.NandaAction.inject(CarePlan.internalid).then(() => {

      this.viewManager.setPopupViewId('diagnosis-delete').then((diagnosisDelete: DiagnosisDelete) => {
        diagnosisDelete.delete.subscribe(() => {
          this.NandaAction.cancel().then(() => {
            this.viewManager.setPopupViewId(null);
            this.refresh();
          });
        });
      });
    });
  }

  deleteInfo(CarePlan) {
    this.NandaAction.inject(CarePlan.internalid, null, ['cancelledBy']).then(() => {
      this.viewManager.setPopupViewId('diagnosis-delete');
    });
  }

  addObjective(CarePlan) {
    this.NandaAction.inject(CarePlan.internalid, null, ['nandaDiag']).then((nanda: Nanda) => {
      this.ObjectiveNandaAction.newObjectiveNanda(nanda, this.PatientEncounter);

      this.viewManager.setPopupViewId('objective').then((objective: Objective) => {
        objective.create.subscribe(() => {
          this.ObjectiveNandaAction.create().then(() => {
            this.viewManager.setPopupViewId(null);
            this.refresh();
          });
        });
      });

    });
  }

  addActivity(carePlan) {
    if (carePlan) {
      this.NandaAction.inject(carePlan.internalid, null, ['nandaDiag']).then(() =>
        this.openFavorite()
      );
    } else {
      this.NandaAction.eject().then(() =>
        this.openFavorite()
      );
    }
  }

  openFavorite() {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    this.router.navigate(['/dashboard', 'favorite', sdlId, 'activity']);
  }

  closeActivity(carePlan) {

    this.conversationActions.put('CarePlan', carePlan);

    this.ObjectiveNandaAction.inject(carePlan.objectiveid, null, ['activity', 'activity.author']).then((objectiveNanda: ObjectiveNanda) => {
      let execution: LEPExecution = this.LEPExecutionAction.newEntity();
      // execution.lepActivity = {internalId: objectiveNanda.activity.internalId, entityName: 'com.phi.entities.act.LEPActivity'};
      //objectiveNanda.activity.frqncy =
      execution.lepActivity = objectiveNanda.activity;
      this.conversationActions.put('LEPExecution', execution);


      this.viewManager.setPopupViewId('objective-close').then((objectiveClose: ObjectiveClose) => {
        if (environment.nurseActivity.clinicalDiary) {
          objectiveClose.save.subscribe(() => {
            execution.note = objectiveNanda.cancellationNote;
            const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
            this.ClinicalDiaryAction.newClinicalDiary(this.PatientEncounter, execution, sdlId);
            this.ClinicalDiaryAction.entity.text = carePlan.title + '\n' + carePlan.subtitle + '\n' + objectiveNanda.cancellationNote;
            this.ClinicalDiaryAction.create().then(() => {
              this.PatientEncounterAction.updateLastClinicalDiary(this.PatientEncounter.internalId).then(() => {
                this.ObjectiveNandaAction.cancelAndChangeStatus().then(() => {
                  this.viewManager.setPopupViewId(null);
                  this.refresh('activities');
                });
              });
            });
          });
        } else {
          objectiveClose.save.subscribe(() => {
            this.ObjectiveNandaAction.cancelAndChangeStatus().then(() => {
              this.viewManager.setPopupViewId(null);
              this.refresh('activities');
            });
          });
        }
      });
    });
  }

  /**
   * Edit with LepActivityEdit, move form favorite to ./components
   */
  editActivity = (carePlan) => {

    if (carePlan.objectiveid != null) { // INvalidate Objective nanada and LepActivity

      this.ObjectiveNandaAction.injectClone(carePlan.objectiveid).then((originalObjectiveNanda: ObjectiveNanda) => {

        // Edit new ObjectiveNanda (clone)
        let newObjectiveNanda: ObjectiveNanda = this.ObjectiveNandaAction.entity;
        this.conversationActions.put('LEPActivityList', new Datamodel([newObjectiveNanda.activity]));

        this.viewManager.setPopupViewId('lep-activity-edit').then((lepActivityEdit: LepActivityEdit) => {
          lepActivityEdit.save.subscribe(() => {
            // Set cancellation note on original LEPActivity
            this.conversationActions.put('LEPActivity', originalObjectiveNanda.activity);
            this.LEPActivityAction.entity = originalObjectiveNanda.activity;
            this.ObjectiveNandaAction.entity = originalObjectiveNanda;

            this.viewManager.setPopupViewId('lep-activity-delete').then((lepActivityDelete: LepActivityDelete) => {
              lepActivityDelete.delete.subscribe(() => {
                //Save all and new
                this.ObjectiveNandaAction.updateAndChangeStatus(newObjectiveNanda).then(() => {
                  this.viewManager.setPopupViewId(null);
                  this.refresh('activities');
                });
              });

            });
          });
        });
      });

    } else { // INvalidate only LepActivity

      this.LEPActivityAction.injectClone(carePlan.internalid).then((originalActivity: LEPActivity) => {

        // Edit new activity (clone)
        let newActivity:LEPActivity = this.LEPActivityAction.entity;
        this.conversationActions.put('LEPActivityList', new Datamodel([newActivity]));

        this.viewManager.setPopupViewId('lep-activity-edit').then((lepActivityEdit: LepActivityEdit) => {
          lepActivityEdit.save.subscribe(() => {

            // Set cancellation note on original activity
            this.conversationActions.put('LEPActivity', originalActivity);
            this.LEPActivityAction.entity = originalActivity;

            this.viewManager.setPopupViewId('lep-activity-delete').then((lepActivityDelete: LepActivityDelete) => {
              lepActivityDelete.delete.subscribe(() => {
                //Save all and new
              this.LEPActivityAction.updateAndChangeStatus(newActivity).then(() => {
                this.viewManager.setPopupViewId(null);
                this.refresh('activities');
              });
            });
            });

          });
        });

      });

    }
  };

  deleteActivity(carePlan) {
    if (carePlan.objectiveid != null) { // INvalidate Objective nanada and LepActivity

      this.ObjectiveNandaAction.inject(carePlan.objectiveid, null, ['activity']).then((objectiveNanda: ObjectiveNanda) => {
        this.conversationActions.put('LEPActivity', objectiveNanda.activity);

        if (carePlan.enddate != null) {

          this.viewManager.setPopupViewId('lep-activity-delete').then((lepActivityDelete: LepActivityDelete) => {
            lepActivityDelete.delete.subscribe(() => {
              this.cancelObjectiveNanda();
            });
          });

        } else {
          this.viewManager.openAlertMessage(
            this.translateService.instant('Confirm'),
            this.translateService.instant('Delete_activity_Warning'),
            true,
            true,
            'OK',
            'Cancel',
            true,
            () => {
              this.cancelObjectiveNanda()
            }
          );
        }
      });

    } else { // INvalidate only LepActivity

      this.LEPActivityAction.inject(carePlan.internalid).then((lep: LEPActivity) => {

        if (carePlan.enddate != null) {

          this.viewManager.setPopupViewId('lep-activity-delete').then((lepActivityDelete: LepActivityDelete) => {
            lepActivityDelete.delete.subscribe(() => {
              this.cancelLEPActivity();
            });
          });

        } else {
          this.viewManager.openAlertMessage(
            this.translateService.instant('Confirm'),
            this.translateService.instant('Delete_activity_Warning'),
            true,
            true,
            'OK',
            'Cancel',
            true,
            () => {
              this.cancelLEPActivity();
            }
          );
        }
      });

    }
  }

  private cancelObjectiveNanda() {
    this.ObjectiveNandaAction.cancelAndChangeStatus().then(() => {
      this.viewManager.setPopupViewId(null);
      this.refresh('activities');
    });
  }

  private cancelLEPActivity() {
    this.LEPActivityAction.cancelAndChangeStatus().then(() => {
      this.viewManager.setPopupViewId(null);
      this.refresh('activities');
    });
  }

  config() {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    this.router.navigate(['/dashboard', 'favorite', sdlId, 'activity', 'configuration']);
  }

  getObjectiveClass(objective) {
    if (objective.cancellationdate != null || (objective.enddate != null && objective.enddate.getTime() < new Date().getTime())) {
      return 'fa fa-times cancelled';
    } else {
      return 'fa fa-check active';
    }
  }

  getActivityClass(activity) {
    if (activity.invalidationdate != null || (activity.enddate != null && activity.enddate.getTime() < new Date().getTime())) {
      return 'fa fa-times cancelled';
    } else {
      return 'fa fa-check active';
    }
  }

  getStatusClass(carePlan) {
    if (carePlan.status === 'cancelled') {
      if (carePlan.modified) {
        return 'column-modified';
      } else {
        return 'column-cancelled';
      }
    } else if (carePlan.status === 'active') {
      return 'column-active';
    } else if (carePlan.status === 'completed') {
      return 'column-completed';
    }
  }

}
