import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate } from '@angular/router';
import { select } from '@angular-redux/store';
import { PatientEncounter } from '../../services/entities/act';
import { ViewManager } from '../../services/view-manager';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class FlexContainerCanActivateService implements CanActivate {

  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;

  constructor(private router: Router,
              private viewManager: ViewManager,
              private translateService: TranslateService) {

    this.PatientEncounter$.subscribe((res) => {
      this.PatientEncounter = res;
    });
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {

    if (!this.PatientEncounter && state.url === '/flex/DrugPrescriber') {
      this.viewManager.openAlertMessage(
        this.translateService.instant('Warning'),
        'Devi selezionare un caso!',
        false,
        true,
        'OK',
        'Cancel',
        true,
        () => {
          this.router.navigate(['/dashboard', 'adt']);
        }
      );
      return false;
    } else {
      return true;
    }

  }
}
