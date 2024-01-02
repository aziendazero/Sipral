/**
 * Created by Daniele Tarticchio on 15/06/2017.
 */
import {Component, Injector, OnInit} from '@angular/core';
import { select } from '@angular-redux/store';
import { Router } from '@angular/router';
import { NurseActivityPanel } from './components/nurse-activity-panel';
import { LepActivity } from './components/lep-activity';
import { NeedsBasedWarning } from './components/needs-based-warning';
import { NurseActivityCancel } from "./components/nurse-activity-cancel";
import { ConversationActions } from '../../actions/conversation.actions';
import { NurseActivityAction } from './actions/nurse-activity-action.service';
import { PatientEncounterActionService,
  LEPActivityActionService,
  LEPExecutionActionService,
  PrescriptionActionService,
  SubstanceAdministrationActionService} from '../../services/actions';
import { ErogationStatus } from './utils/erogation-status';
import { BaseForm } from '../../widgets/form/base-form';
import { ViewManager } from '../../services/view-manager';
import { environment } from '../../../environments/environment';




@Component({
  templateUrl: './nurse-activity.html',
  styleUrls: ['./nurse-activity.component.scss']
})

export class NurseActivityComponent extends BaseForm implements OnInit {

  @select(['conversation', 'selectedWorkShift']) selectedWorkShift$;
  selectedWorkShift;
  @select(['conversation', 'NurseActivityAdditionalworkShift']) NurseActivityAdditionalworkShift$;
  NurseActivityAdditionalworkShift;
  @select(['conversation', 'NurseActivityList']) NurseActivityList$;
  NurseActivityList;
  @select(['conversation', 'NurseActivity']) NurseActivity$;
  NurseActivity;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter;
  @select(['conversation', 'SubstanceAdministration']) SubstanceAdministration$;
  SubstanceAdministration;

  mode: 'PATIENT' | 'WARD' = 'PATIENT';

  filterPharma = true;
  filterInfu = true;
  filterExecution = true;

  searchVisible = true;

  environment = environment;

  constructor(injector: Injector,
              public conversationActions: ConversationActions,
              public NurseActivityAction: NurseActivityAction,
              private viewManager: ViewManager,
              private router: Router,
              private PatientEncounterAction: PatientEncounterActionService,
              private LEPActivityAction: LEPActivityActionService,
              private LEPExecutionAction: LEPExecutionActionService,
              private PrescriptionAction: PrescriptionActionService,
              private SubstanceAdministrationAction: SubstanceAdministrationActionService,
              public erogationStatus: ErogationStatus) {
    super(injector);

    this.selectedWorkShift$.subscribe(res => this.selectedWorkShift = res);
    this.NurseActivityAdditionalworkShift$.subscribe(res => this.NurseActivityAdditionalworkShift = res);
    this.NurseActivityList$.subscribe(res => this.NurseActivityList = res);
    this.NurseActivity$.subscribe(res => this.NurseActivity = res);
    this.PatientEncounter$.subscribe(res => this.PatientEncounter = res);
    this.SubstanceAdministration$.subscribe(res => this.SubstanceAdministration = res);
  }

  ngOnInit() {
    if (this.PatientEncounter && this.PatientEncounter.code.code === 'IMP') {
      this.init();
    } else {
      this.router.navigate(['/dashboard', 'adt']); //no encounter, go to adt
    }
  }

  init() {
    // console.log('Init activity list');
    this.NurseActivityAction.cleanRestrictions();
    let patientEncounter = this.PatientEncounter;
    if (patientEncounter) {
      this.NurseActivityAction.equal['encounterId'] = patientEncounter['internalId'];
      if (patientEncounter.therapy && patientEncounter.therapy.length > 0) {
        let therapyId: number = patientEncounter.therapy[0].internalId;
        this.NurseActivityAction.equal['therapyId'] = therapyId;
      }

      let low: Date = new Date();
      low.setHours(0, 0, 0, 0);
      let high: Date = new Date();
      high.setHours(23, 59, 59, 999);

      this.NurseActivityAction.equal['plannedDateFrom'] = low;
      this.NurseActivityAction.equal['plannedDateTo'] = high;

      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      this.NurseActivityAction.equal['sdlocId'] = sdlId;

      this.filterPharma = true;
      this.filterInfu = true;
      this.filterExecution = true;

      this.NurseActivityAction.init();
    } else {
      console.log('no encounter injected, the router should never bring you here. Check the code.');
    }
  }

  refresh() {

    delete this.NurseActivityAction.equal["onlyExecutions"];
    delete this.NurseActivityAction.equal["prescriptionCode.code"];
    delete this.NurseActivityAction.equal["onlySubministrations"];

    // Filter type
    if (!this.environment.nurseActivity.onlyExecutions) {
      if (!this.filterPharma && !this.filterInfu && !this.filterExecution) {
        //Nessun tipo selezionato, cancella risultati precedenti e non eseguire la query
        this.NurseActivityAction.cleanResults();
        return;
      }
      else if (!this.filterPharma && !this.filterInfu) {
        //Se Farmaci e Infusioni deselezionati, non fare query su somministrazioni
        delete this.NurseActivityAction.equal["therapyId"];
        this.NurseActivityAction.equal["onlyExecutions"] = true;
      }
      else if (this.filterPharma && !this.filterInfu) {
        //Se selezionati solo Farmaci filtra somministrazioni con code PHARMA
        this.NurseActivityAction.equal["prescriptionCode.code"] = "PHARMA";
      }
      else if (!this.filterPharma && this.filterInfu) {
        //Se selezionati solo Infusioni filtra somministrazioni con code INFU
        this.NurseActivityAction.equal["prescriptionCode.code"] = "INFU";
      }

      if (!this.filterExecution) {
        //Se Attività assistenziali deselezionati, non fare query su esecuzioni
        delete this.NurseActivityAction.equal["encounterId"];
        this.NurseActivityAction.equal["onlySubministrations"] = true;
      }
    } else {
      this.NurseActivityAction.equal["onlyExecutions"] = true;
    }

    this.NurseActivityAction.refresh();
  }

  ie(entity, conversationName): Promise<any> {
    if (entity != this.NurseActivity) {
      this.conversationActions.put(conversationName, entity);
    } else {
      this.conversationActions.remove(conversationName);
    }
    return null;
  }

  shitchMode(selectedTabIndex) {
    if (selectedTabIndex === 0) {
      this.mode = 'PATIENT';
      this.NurseActivityAction.equal['encounterId'] = this.PatientEncounter['internalId'];
      if (this.PatientEncounter.therapy && this.PatientEncounter.therapy.length > 0) {
        let therapyId: number = this.PatientEncounter.therapy[0].internalId;
        this.NurseActivityAction.equal['therapyId'] = therapyId;
      }
    } else {
      this.mode = 'WARD';
      delete this.NurseActivityAction.equal['encounterId'];
      delete this.NurseActivityAction.equal['therapyId'];
    }
    this.refresh();
  }

  workShiftChange() {
    //change date instance to update the widget
    if (this.selectedWorkShift && this.selectedWorkShift['from'] && this.selectedWorkShift['to']) {
      this.NurseActivityAction.equal['plannedDateFrom'] = new Date();
      this.NurseActivityAction.equal['plannedDateTo'] = new Date();

      this.NurseActivityAction.equal['plannedDateFrom'].setHours(this.selectedWorkShift['from'].getHours(), this.selectedWorkShift['from'].getMinutes(), 0, 0);
      this.NurseActivityAction.equal['plannedDateTo'].setHours(this.selectedWorkShift['to'].getHours(), this.selectedWorkShift['to'].getMinutes(), 0, 0);

      if (this.selectedWorkShift['from'] > this.selectedWorkShift['to']) { // across 2 days
        this.NurseActivityAction.equal['plannedDateTo'].setDate(this.NurseActivityAction.equal['plannedDateTo'].getDate() + 1);
      }
    }
  }

  async erogate(nurseActivity, event, immediate = false) {
    event.stopPropagation();
    nurseActivity.statusTranslated = this.erogationStatus.getErogationString(nurseActivity.status);
    this.conversationActions.put('NurseActivity', nurseActivity);

    if (nurseActivity.type === "S") {

      await this.LEPExecutionAction.eject();

      let substanceAdministration = await this.SubstanceAdministrationAction.inject(nurseActivity.internalid, null, ['author']);/*.then((substanceAdministration) => {*/
      let prescription = await this.PrescriptionAction.inject(nurseActivity.parent.internalid, null, ['statusCode', 'routeCode', 'author']);/*.then((prescription) => {*/

        if (!substanceAdministration.text) {
          substanceAdministration.text = {};
        }

        if (nurseActivity.erogable) {
          if (immediate) {
            await this.SubstanceAdministrationAction.erogate(new Date(), {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'DONE'}, prescription.continuous);/*.then(() =>*/
              this.refresh()
            // );
          } else {
            const asNeeded = await this.NurseActivityAction.getAsNeededDetails(nurseActivity);/*.then((asNeeded) => {*/
              if (asNeeded) {

                this.viewManager.setPopupViewId('needs-based-warning').then((needsBasedWarning: NeedsBasedWarning) => {
                  needsBasedWarning.ok.subscribe(() => {

                    this.viewManager.setPopupViewId('nurse-activity').then((nurseActivityPanel: NurseActivityPanel) => {
                      nurseActivityPanel.save.subscribe(() =>
                        this.refresh()
                      );
                    });
                  });
                });
              } else {
                this.viewManager.setPopupViewId('nurse-activity').then((nurseActivityPanel: NurseActivityPanel) => {
                  nurseActivityPanel.save.subscribe(() =>
                    this.refresh()
                  );
                });
              }
            // });
          }

        } else if (nurseActivity.removable) {
          if (immediate) {
              this.viewManager.setPopupViewId('nurse-activity-cancel').then((nurseActivityCancel: NurseActivityCancel) => {
                nurseActivityCancel.ok.subscribe(() => {
                this.viewManager.setPopupViewId(null);
                this.SubstanceAdministrationAction.cancel(nurseActivityCancel.note, this.erogationStatus.getErogationString(nurseActivity.status)).then(() =>
                  this.refresh()
                );
              });
            });
          } else {
            this.viewManager.setPopupViewId('substance-administration-detail');
          }
        } else if (nurseActivity.erogated) {
          this.viewManager.setPopupViewId('substance-administration-detail');
        }
      // });
    // });
    } else if (nurseActivity.type == "E") {

      if (!environment.nurseActivity.onlyExecutions) {
        await this.SubstanceAdministrationAction.eject();
      }

      await this.LEPExecutionAction.inject(nurseActivity.internalid, null, ['lepActivity', 'lepActivity.author', 'lepActivity.responsibleRole', 'lepActivity.supportRole']);/*.then(() => {*/

        if (nurseActivity.erogable) {
          if (immediate) {
            this.LEPExecutionAction.erogate(new Date(), {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'DONE'}).then(() =>
              this.refresh()
            );
          } else {
            this.viewManager.setPopupViewId('nurse-activity').then((nurseActivityPanel: NurseActivityPanel) => {
              nurseActivityPanel.save.subscribe(() =>
                this.refresh()
              );
            });
          }

        } else if (nurseActivity.removable) {
          if (immediate) {
            this.viewManager.setPopupViewId('nurse-activity-cancel').then((nurseActivityCancel: NurseActivityCancel) => {
              nurseActivityCancel.ok.subscribe(() => {
                this.viewManager.setPopupViewId(null);
                this.LEPExecutionAction.cancel(nurseActivityCancel.note, this.erogationStatus.getErogationString(nurseActivity.status)).then(() =>
                  this.refresh()
                );
              });
            });
          } else {
            this.viewManager.setPopupViewId('lep-execution-detail');
          }
        } else if (nurseActivity.erogated) {
          this.viewManager.setPopupViewId('lep-execution-detail');
        }
      // });
    }
  }

  /**
   * Create a new manual LEPActivity and its LEPExecution
   */
  addNurseActivity() {

    this.LEPActivityAction.newLEPActivity(this.PatientEncounter, null, true);

    this.viewManager.setPopupViewId('lep-activity').then((lepActivity: LepActivity) => {
      lepActivity.create.subscribe(() => {
        this.viewManager.setPopupViewId(null);
        this.LEPActivityAction.create().then(() =>
          this.refresh()
        );
      });
    });
  }

  toggleSearch() {
    this.searchVisible = !this.searchVisible;
  }

  getDescriptionList(description:string){
    // console.log(description);
    if(description!=null)
      return description.split('\n');
    else
      return new Array();
  }

  getStatusBackground(activity) {
    let almostNowPast: Date = new Date();
    let almostNowFuture: Date = new Date();

    almostNowPast.setMinutes(almostNowPast.getMinutes() - 30);
    almostNowFuture.setMinutes(almostNowFuture.getMinutes() + 30);

    if (activity.parent.creationdate.getTime() > almostNowPast.getTime()
        && activity.planneddate != null
        && activity.planneddate.getTime() < almostNowFuture.getTime()
        && !activity.erogated
        && activity.cancellationdate == null) {

      return 'ALMOST_NOW';
    }
    return '';
  }

  getIconClass(activity) {
    if (activity.type === 'S') {
      return activity.parent.code;
    } else {
      if (activity.objectiveid !== null) {
        return 'fa fa-star tableCell';
      } else {
        return 'fa fa-shopping-bag tableCell';

      }
    }
  }

  getStyle(descriptionbold, index){
    if(descriptionbold!=null){
     if(descriptionbold.split(',')[index] == '0'){
       return 'bold'
     }else{
       return '';
     }
    }else{
      return '';
    }
  }
}
