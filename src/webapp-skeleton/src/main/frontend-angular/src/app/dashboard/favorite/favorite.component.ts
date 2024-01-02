import { Component, Injector, OnInit, OnDestroy, HostBinding } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { TemplateDosageActionService, FavoriteTabActionService, FavoriteProfileActionService,
  FavoriteSectionActionService, ServiceDeliveryLocationActionService, PrescriptionActionService, LEPActivityActionService } from '../../services/actions';
import { PatientEncounter, Nanda, LEPActivity } from '../../services/entities/act';
import { ServiceDeliveryLocation } from '../../services/entities/role';
import { Prescription } from '../../services/entities/base-entity';
import { CodeValue } from '../../services/entities/data-types/code-value';
import { FavoriteTab } from '../../services/entities/favorite-tab';
import { FavoriteSection } from '../../services/entities/favorite-section';
import { FavoriteProfile } from '../../services/entities/favorite-profile';
import { FavoriteTabComponent } from './favorite-tab.component';
import { FavoriteSectionComponent } from './favorite-section.component';
import { Datamodel } from '../../services/datamodel/datamodel';
import { DictionaryManager } from '../../services/dictionary-manager';
import { ConversationActions } from '../../actions/conversation.actions';
import { LepTree } from './activity/lep-tree';
import { LepActivityEdit } from './activity/lep-activity-edit';
import { BaseForm } from '../../widgets/form/base-form';
import { Config } from '../../store/config.reducer';
import { SelectItem } from '../../services/datamodel/select-item';
import { ViewManager } from '../../services/view-manager';
import { color2hex } from '../../services/converters/any.converter';
import { environment } from '../../../environments/environment';


@Component({
  templateUrl: './favorite.html',
  styleUrls: ['./favorite.component.scss']
})
export class FavoriteComponent extends BaseForm implements OnInit, OnDestroy {
  @HostBinding('class') clazz = 'fullHeightWidth';

  @select(['conversation']) conversation$;
  FavoriteTabList: Datamodel;
  FavoriteTab: FavoriteTab;
  ServiceDeliveryLocation: ServiceDeliveryLocation;
  PatientEncounter: PatientEncounter;
  Nanda: Nanda;
  LEPActivityList: Datamodel;
  favoriteConfiguration = false;
  favoriteType: 'THERAPY' | 'ACTIVITY' = 'THERAPY';
  dischargeMode;

  @select(['config']) config$;
  enabledSdlocs: Array<SelectItem>;

  serviceDeliveryLocationId;
  columnList;

  typeCv: CodeValue;

  constructor(injector: Injector,
              private router: Router,
              private route: ActivatedRoute,
              private viewManager: ViewManager,
              private TemplateDosageAction: TemplateDosageActionService,
              private ServiceDeliveryLocationAction: ServiceDeliveryLocationActionService,
              private FavoriteTabAction: FavoriteTabActionService,
              private FavoriteProfileAction: FavoriteProfileActionService,
              private FavoriteSectionAction: FavoriteSectionActionService,
              private PrescriptionAction: PrescriptionActionService,
              private LEPActivityAction: LEPActivityActionService,
              private conversationActions: ConversationActions,
              private dictionaryManager: DictionaryManager,
              private translateService: TranslateService) {

    super(injector);

    this.conversation$.subscribe(res => {
      this.FavoriteTabList = res.FavoriteTabList;
      this.FavoriteTab = res.FavoriteTab;
      if (this.FavoriteTab && this.FavoriteTab.numColumn) {
        this.columnList = [];
        for (let i = 1; i <= this.FavoriteTab.numColumn; i++) {
          this.columnList.push(i);
        }
      }
      this.ServiceDeliveryLocation = res.ServiceDeliveryLocation;
      this.PatientEncounter = res.PatientEncounter;
      this.Nanda = res.Nanda;
      this.LEPActivityList = res.LEPActivityList;
      this.favoriteConfiguration = res.favoriteConfiguration;
      this.favoriteType = res.favoriteType;
      this.dischargeMode = res.dischargeMode;
    });

    this.config$.subscribe((cfg: Config) => {
      if (cfg.sdLocs) {
        this.enabledSdlocs = cfg.sdLocs
          .filter((sdl => sdl.type === 'WARD' || sdl.hybrid))
          .map((sdl) => ({
          label: sdl.text,
          value: +sdl.id,
        }));
      }
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {

      //'dashboard/favorite/:servideDeliveryLocationId/:type/:configurationMode'

      this.serviceDeliveryLocationId = parseInt(params['servideDeliveryLocationId']);

      if (this.serviceDeliveryLocationId &&
        (!this.ServiceDeliveryLocation || this.ServiceDeliveryLocation.internalId !== this.serviceDeliveryLocationId)) {
        this.ServiceDeliveryLocationAction.inject(this.serviceDeliveryLocationId);
      }

      if (params['type']) {
        this.conversationActions.put('favoriteType',  params['type'].toUpperCase());

        this.dictionaryManager.get('PHIDIC:FavoriteType').then((codeValues: Array<CodeValue>) =>
          this.typeCv = codeValues.find((cv: CodeValue) => cv.code === this.favoriteType)
        );
      }
      if (params['configurationMode']) {
        this.conversationActions.put('favoriteConfiguration', true);
      } else {
        this.conversationActions.put('favoriteConfiguration', false);
      }

      this.readFavoriteTabs();
      this.TemplateDosageAction.readTemplateDosages(this.serviceDeliveryLocationId, this.favoriteType);

    });
  }

  ngOnDestroy() {
    this.conversationActions.remove('FavoriteTab');
    this.conversationActions.remove('FavoriteTabList');
    this.conversationActions.remove('favoriteConfiguration');
  }

  // Read TemplateDosages
  // private readTemplateDosages(currentSdlId, code = 'THERAPY') {
  //   this.TemplateDosageAction.cleanRestrictions();

  //   this.TemplateDosageAction.equal['isActive'] = true;
  //   this.TemplateDosageAction.equal['serviceDeliveryLocation.internalId'] = currentSdlId;
  //   this.TemplateDosageAction.equal['code.code'] = code;
  //   this.TemplateDosageAction.orderBy['title'] = 'ascending';
  //   this.TemplateDosageAction.read();
  // }

  // Read the tabs
  public readFavoriteTabs(): void {
    this.FavoriteTabAction.cleanRestrictions();

    this.FavoriteTabAction.equal['isActive'] = true;
    this.FavoriteTabAction.equal['serviceDeliveryLocation.internalId'] = this.serviceDeliveryLocationId;
    this.FavoriteTabAction.equal['typeCode.code'] = this.favoriteType;
    this.FavoriteTabAction.orderBy['sortOrder'] = 'ascending';
    this.FavoriteTabAction.read();
  }

  ie(entity, conversationName): Promise<any> {

    let loads = ['activity', 'activity.nandaLep'];
    if (!environment.nurseActivity.onlyExecutions) {
      loads.push('prescription');
      loads.push('prescription.code');
    }

    return this.FavoriteProfileAction.inject(entity.internalId, null, loads)
      .then( (favoriteProfile: FavoriteProfile) => {
        if (favoriteProfile) {
          let dm:Datamodel;

          if (this.favoriteType === 'THERAPY') {

            if (this.favoriteConfiguration) {
              dm = new Datamodel(favoriteProfile.prescription);
            } else {
              let newPrescriptionsFromProfile:Array<Prescription> = favoriteProfile.prescription.map((p: Prescription) => {
                let newPrescription: Prescription = this.PrescriptionAction.clone(p);
                newPrescription.period = {high: null, low: new Date()};
                if (newPrescription.extemporaneous || newPrescription.urgent) {
                  newPrescription.period.high = new Date();
                  newPrescription.period.high.setMinutes(newPrescription.period.high.getMinutes() + 1);
                }
                return newPrescription;
              });
              dm = new Datamodel(newPrescriptionsFromProfile);
            }
            this.conversationActions.put('SelectedPrescriptionList', dm);
            this.conversationActions.put('Prescription', dm.entities[0]);

            this.viewManager.setPopupViewId('prescription')

          } else if (this.favoriteType === 'ACTIVITY') {

            if (this.favoriteConfiguration) {
              dm = new Datamodel(favoriteProfile.activity);
            } else {
              dm = new Datamodel(favoriteProfile.activity.map((a: LEPActivity) =>
                this.LEPActivityAction.clone(a, this.PatientEncounter, this.Nanda)
              ));
            }
            this.conversationActions.put('LEPActivityList', dm);
            this.editActivity();
          }
        }
      }
    );
  }

  sdlChange() {
    if (this.serviceDeliveryLocationId) {
      const path = ['/dashboard', 'favorite', this.serviceDeliveryLocationId, this.favoriteType.toString().toLowerCase()];
      if (this.favoriteConfiguration) {
        path.push('configuration');
      }
      this.router.navigate(path);
    }
  }

  goToDrugPrescriber() {
    if (this.favoriteType === 'THERAPY') {
      if (this.dischargeMode) {
        this.router.navigate(['/dashboard', 'drug-prescriber-discharge']);
      } else {
        this.router.navigate(['/dashboard', 'drug-prescriber']);
      }
    } else if (this.favoriteType === 'ACTIVITY') {
      this.router.navigate(['/dashboard', 'activity-prescriber']);
    }
  }

  newTabPanelAction() {
    this.FavoriteTabAction.newEntity();
    this.FavoriteTab.serviceDeliveryLocation = {
      internalId: this.serviceDeliveryLocationId,
      entityName: 'com.phi.entities.role.ServiceDeliveryLocation',
      name: this.ServiceDeliveryLocation.name
    };
    this.FavoriteTab.typeCode = this.typeCv;
    this.FavoriteTab.sortOrder = this.FavoriteTabList.entities.length+1;
    this.FavoriteTab.numColumn = 2;

    this.viewManager.setPopupViewId('favorite-tab').then((favoriteTabComponent: FavoriteTabComponent) => {
      favoriteTabComponent.closeEvent.subscribe(() =>
        this.FavoriteTabAction.read()
      );
    });
  }

  modTabPanelAction(favoriteTab: FavoriteTab) {
    this.FavoriteTabAction.inject(favoriteTab.internalId, null, ['serviceDeliveryLocation']).then(() =>
      this.viewManager.setPopupViewId('favorite-tab').then((favoriteTabComponent: FavoriteTabComponent) => {
        favoriteTabComponent.closeEvent.subscribe(() =>
          this.FavoriteTabAction.read()
        );
      })
    );
  }

  deleteTabPanelAction(favoriteTab: FavoriteTab) {
    this.viewManager.openAlertMessage(
      this.translateService.instant('Confirm'),
      this.translateService.instant('Delete_tab_Warning'),
      true,
      true,
      'OK',
      'Cancel',
      true,
      () => {
        this.FavoriteTabAction.entity = favoriteTab;
          this.FavoriteTabAction.delete().then(() =>
            this.FavoriteTabAction.read()
          );
      }
    );
  }

  editTemplateDosage() {
    this.viewManager.setPopupViewId('template-dosage');
  }

  /**
   * Create a new pharmacologic Prescription with code Pharma
   */
  addManually(isProfile = false) {
    if (this.favoriteType === 'THERAPY')  {
      this.PrescriptionAction.eject();
      this.conversationActions.remove('SelectedPrescriptionList');
      this.conversationActions.remove('PrescriptionHistoryList');

      this.viewManager.setPopupViewId('search-medicine');
    }
  }

  /**
   * Create a new infusion Prescription with code INFU
   */
  addManuallyInfu(isProfile = false) {
    if (this.favoriteType === 'THERAPY')  {
      this.conversationActions.remove('PrescriptionHistoryList');

      let dm: Datamodel = new Datamodel([]);
      let p: Prescription = this.PrescriptionAction.newPrescription(null, null, 'INFU');
      dm.entities.push(p);
      this.conversationActions.put('Prescription',  p);
      this.conversationActions.put('SelectedPrescriptionList', dm);

      this.viewManager.setPopupViewId('prescription');
    }
  }

  /**
   * Create a new LEPActivity
   */
  addActivity = (isNew = false) => {
    let lep: LEPActivity;
    if (!this.favoriteConfiguration) {
      lep = this.LEPActivityAction.newLEPActivity(this.PatientEncounter, this.Nanda);
    } else { // favoriteConfiguration
      lep = this.LEPActivityAction.newLEPActivity(null, null);
    }
    if (isNew || !this.LEPActivityList) {
      this.conversationActions.put('LEPActivityList', new Datamodel([]));
    }
    this.LEPActivityList.entities.push(lep);

    this.viewManager.setPopupViewId('lep-tree').then((lepTree: LepTree) => {
      lepTree.select.subscribe(this.editActivity);
    });
  }

  editActivity = () => {

    this.viewManager.setPopupViewId('lep-activity-edit').then((lepActivityEdit: LepActivityEdit) => {

      lepActivityEdit.save.subscribe(() => {
        if (!this.favoriteConfiguration) {
          this.LEPActivityAction.createAll(this.LEPActivityList.entities).then(() =>
            this.viewManager.setPopupViewId(null)
          );
        } else { //favoriteConfiguration
          this.FavoriteProfileAction.entity.activity = this.LEPActivityList.entities;
          return this.FavoriteProfileAction.create().then(() => {
            this.viewManager.setPopupViewId(null);
            this.FavoriteTabAction.read();
          });
        }
      });

      lepActivityEdit.addActivity.subscribe(this.addActivity);
    });
  };

  addSection(FavoriteTab, column) {
    this.FavoriteSectionAction.newEntity();
    this.conversationActions.put('column', column);

    this.viewManager.setPopupViewId('favorite-section').then((favoriteSectionComponent: FavoriteSectionComponent) => {
      favoriteSectionComponent.save.subscribe(() =>
        this.FavoriteTabAction.read()
      );
    });
  }

  editSection(FavoriteSection) {
    this.conversationActions.put('FavoriteSection', FavoriteSection);
    this.viewManager.setPopupViewId('favorite-section').then((favoriteSectionComponent: FavoriteSectionComponent) => {
      favoriteSectionComponent.save.subscribe(() =>
        this.FavoriteTabAction.read()
      );
    });
  }

  deleteSection(FavoriteSection) {
    this.viewManager.openAlertMessage(
      this.translateService.instant('Confirm'),
      this.translateService.instant('Delete_section_Warning'),
      true,
      true,
      'OK',
      'Cancel',
      true,
      () => {
        this.FavoriteSectionAction.entity = FavoriteSection;
          this.FavoriteSectionAction.delete().then(() =>
            this.FavoriteTabAction.read()
          );
      }
    );
  }

  /**
   * Create a new FavoriteProfile
   * @param favoriteSection
   */
  addProfile(favoriteTab: FavoriteTab, favoriteSection: FavoriteSection) {

    this.FavoriteProfileAction.newFavoriteProfile(favoriteSection);

    if (favoriteTab.typeCode.code === 'THERAPY') {

      if (favoriteTab.subTypeCode.code === 'PHARMA') {
        this.addManually(true);
      } else if (favoriteTab.subTypeCode.code === 'INFU') {
        this.addManuallyInfu(true);
      }

    } else if (favoriteTab.typeCode.code === 'ACTIVITY') {
      this.conversationActions.put('LEPActivityList', new Datamodel([]));
      this.addActivity();
    }

  }

  editProfile(favoriteProfile: FavoriteProfile) {
    this.ie(favoriteProfile, 'FavoriteProfile');
  }

  deleteProfile(favoriteProfile: FavoriteProfile) {
    this.viewManager.openAlertMessage(
      this.translateService.instant('Confirm'),
      this.translateService.instant('Delete_profile_Warning'),
      true,
      true,
      'OK',
      'Cancel',
      true,
      () => {
        this.FavoriteProfileAction.entity = favoriteProfile;
          this.FavoriteProfileAction.delete().then(() =>
            this.FavoriteTabAction.read()
          );
      }
    );
  }

  color2hex = color2hex;

  columns2Percentage(tot) {
    let ret = 100 / tot;
    return ret + '%';
  }

}
