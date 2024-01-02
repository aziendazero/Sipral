import { Component, Inject, OnInit, AfterViewChecked, OnDestroy, NgZone, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select } from '@angular-redux/store';
import { ConfigActions } from '../actions/config.action';
import '../legacy/jsfajax-minimal.js';
import Phi from '../legacy/js';
import { ProcessActions } from '../actions/process.actions';
import { AuthGuard } from 'app/services';
import { BannerActions } from '../actions/banner.action';
import { PhiTree } from '../legacy/tree';
import { PkNetService } from '../services/sign/pk-net.service';
import { environment } from 'environments/environment';
import { Config } from 'app/store/config.reducer';
import { CalamaioService } from 'app/services/sign/calamaio.service';

@Component({
  selector: 'phi-page-container',
  templateUrl: './jsf-container.component.html',
  styleUrls: ['./jsf-container.component.scss'],
  encapsulation: ViewEncapsulation.None // questo fa si che il css associato diventi globale
})
export class JsfContainerComponent implements OnInit, AfterViewChecked, OnDestroy {

  path: string;
  private sub: any;
  viewState: string;
  notCalled = true;
  baseUrl: string;
  phi: Phi;

  @select(['process', 'cid']) cid$;
  cid: any;
  @select(['process', 'current']) currentProcess$;
  currentProcess;
  @select(['process', 'dashboard']) targetDashboard$;
  targetDashboard;

  @select(['config']) config$;
  config: Config;

  constructor(@Inject('apiUrl') protected apiUrl,
              private route: ActivatedRoute,
              private zone: NgZone,
              private processActions: ProcessActions,
              private authGuard: AuthGuard,
              private ConfigActions: ConfigActions,
              private BannerAction: BannerActions,
              private phiTree: PhiTree,
              private pkNetService: PkNetService,
              private calamaioSignService: CalamaioService
  ) {

    this.cid$.subscribe(res => this.cid = res);
    this.currentProcess$.subscribe((res) => this.currentProcess = res);
    this.targetDashboard$.subscribe((res) => this.targetDashboard = res);
    
    this.config$.subscribe((cfg: Config) => this.config = cfg);

    this.phi = new Phi(this.cid);
    window['goHome'] = this.goHome.bind(this);
    window['focusFirstElement'] = this.phi.focusFirstElement.bind(this.phi);
    // window['preventNavigation'] = this.phi.preventNavigation.bind(this.phi);
    window['manageTables'] = this.phi.manageTables.bind(this.phi);
    window['openPopup'] = this.phi.openPopup.bind(this.phi);
    window['openInNewTab'] = this.phi.openInNewTab.bind(this.phi);
    window['openFormPopup'] = this.phi.openFormPopup.bind(this.phi);
    window['openWindow'] = this.phi.openWindow.bind(this.phi);
    window['showDateTimePicker'] = this.phi.showDateTimePicker.bind(this.phi);
    window['setDefaultValue'] = this.phi.setDefaultValue.bind(this.phi);
    window['filterListBox'] = this.phi.filterListBox.bind(this.phi);
    window['filterTbl'] = this.phi.filterTbl.bind(this.phi); // datagrid widget
    window['openSomethingChangedPopup'] = this.phi.openSomethingChangedPopup.bind(this.phi);
    window['openCancelConfirmPopup'] = this.phi.openCancelConfirmPopup.bind(this.phi);
    window['checkSomethingChanged'] = this.phi.checkSomethingChanged.bind(this.phi);
    window['changeGroupCheckBoxRowsStyle'] = this.phi.changeGroupCheckBoxRowsStyle.bind(this.phi);
    window['findTargetWidgets'] = this.phi.findTargetWidgets.bind(this.phi);
    window['stopPropagation'] = this.phi.stopPropagation.bind(this.phi);
    window['scrollToPosition'] = this.phi.scrollToPosition.bind(this.phi);
    window['focusIt'] = this.phi.focusIt.bind(this.phi);
    window['selRadio'] = this.phi.selRadio.bind(this.phi);
    window['selRow'] = this.phi.selRow.bind(this.phi);

    // UPLOAD DOCUMENTS
    window['receiveMessage'] = this.phi.receiveMessage.bind(this.phi);
    window['submitDocument'] = this.phi.submitDocument.bind(this.phi);
    window['upload'] = this.phi.upload.bind(this.phi);
    window['uploadReportHeader'] = this.phi.uploadReportHeader.bind(this.phi);
    window['uploadAttachment'] = this.phi.uploadAttachment.bind(this.phi);

    // DATE IVL WIDGET
    window['getIdPrefix'] = this.phi.getIdPrefix.bind(this.phi);
    window['dateCompare'] = this.phi.dateCompare.bind(this.phi);

    // WIDGET SLIDER
    window['manageSlider'] = this.phi.manageSlider.bind(this.phi);

    window['initHBSTree'] = this.phiTree.initHBSTree.bind(this.phiTree);
    window['initTreeDictionary'] = this.phiTree.initTreeDictionary.bind(this.phiTree);

    window['showDashboard'] = this.showDashboard.bind(this);

    window['Phi'] = this.phi;
    window['PhiTree'] = this.phiTree;
    window['Tree'] = this.phiTree; // FIXME KLINIK

    window['startAjaxReq'] = this.startAjaxReq.bind(this);
    window['stopAjaxReq'] = this.stopAjaxReq.bind(this);

    // sign
    window['pkNetSign'] = this.pkNetService.pkNetSign.bind(this.pkNetService);
    window['pkNetSignXml'] = this.pkNetService.pkNetSignXml.bind(this.pkNetService);
    window['calamaioSign'] = this.calamaioSignService.calamaioSign.bind(this.calamaioSignService);

    // FORMAT
    window['isNumeric'] = this.phi.isNumeric.bind(this.phi);
    window['isRightFormat'] = this.phi.isRightFormat.bind(this.phi);
    window['eventKey'] = this.phi.eventKey.bind(this.phi);
    window['getInputSelection'] = this.phi.getInputSelection.bind(this.phi);

    // ARMONIA WEB
    window['openArmoniaWeb'] = this.phi.openArmoniaWeb.bind(this.phi);
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.path = params['path'];
      this.baseUrl = this.apiUrl + 'home-jsf.seam';

      this.processActions.setCurrentProcess(this.path);

      this.processActions.getForm()
        .then(response => {
          if (response != null) {
            this.viewState = response.viewState || ''; // match[1];
            this.phi.lang = response.lang;
          }
        })
        .then(() => this.phi.checkSomethingChanged());
    });
  }

  ngAfterViewChecked() {
    if (this.viewState != null && this.notCalled) {

       document.getElementById('_viewRoot:status.start')['onstart'] = this.startAjaxReq.bind(this);
       document.getElementById('_viewRoot:status.stop')['onstop'] = this.stopAjaxReq.bind(this);

      window['A4J'].AJAX.Submit('hf', null,
        {
          'similarityGroupingId': 'hf:startProcess',
          'parameters': {
            'ajaxSingle': 'hf:startProcess',
            'hf:startProcess': 'hf:startProcess',
            'currentProcess': this.path
          },
          'actionUrl': this.baseUrl
        });
      this.notCalled = false;
    }
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  startAjaxReq() {
    this.phi.startAjaxReq();
  }

  stopAjaxReq() {
    this.phi.stopAjaxReq();

    const convIdInput: HTMLInputElement = <HTMLInputElement>document.getElementById('conversationId');
    const newConversationId: number = parseInt(convIdInput.value);

    if (newConversationId !== this.cid) {
      console.log('NEW CONVERSATION cid ' + newConversationId);

      this.processActions.newCid(newConversationId);

      if (environment.openMenuAtHome) {
        this.ConfigActions.toggleMenu();
      }
      // zone.run fixes change detection!!
      this.zone.run(() => {
        return this.authGuard.checkRelogin(this.config.employeeRoleCode === '1')
          .then(resp => {
            if (resp.reloginUrl) {
              this.authGuard.needRelogin(resp);
            } else {
              this.processActions.afterPocessEnded();
              }
            });
      });
    } else {
      // Refresh banner always but not on go home to fix: GenericJDBCException:
      // could not initialize a collection: [com.phi.entities.act.PatientEncounter.therapy#xxxx]
      this.zone.run(() =>
        this.BannerAction.refresh()
      );
    }
  }

  /**
   * End process called by javascript inside jsf form
   * @param event
   */
  goHome(event) {
    if (event != null && typeof event !== 'undefined') {
      event.stopPropagation();
    }

    if (environment.openMenuAtHome) {
      this.ConfigActions.toggleMenu();
    }

    this.processActions.endProcess(this.currentProcess).then(resp => {
      this.authGuard.needRelogin(resp);
    });

    return false;
  }

  removeErrorMessages() {
    let errors = document.getElementById('errorMenuErrors');
    if (errors && errors.firstChild) {
      let element = <HTMLElement> errors.firstChild;
      element.innerHTML = '<dt></dt>';
    }
/*  RESET AS WANTED BY RICHFACES:
    <div xmlns="http://www.w3.org/1999/xhtml" id="errorMenu">
      <div id="errorMenuErrors">
        <dl id="j_id4" class="rich-messages" style="display: none;">
          <dt></dt>
        <div>
      </div>
    </div>
*/
  }

  /**
   * Open Angular dashboard from process
   * @param moduleTarget
   */
  showDashboard(moduleTarget) {
    if (moduleTarget === 'swf/modules/DrugPrescriber.swf?prescriptionDischarge=true') {
      // Called by MOD_Inpatient/customer_VCO/FORMS/f_discharge_letter_dip_chir.mmgp
      this.processActions.setDashboard('dashboard/drug-prescriber-discharge');
    } else {
      this.processActions.setDashboard(moduleTarget);
    }
  }

}
