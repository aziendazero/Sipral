import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate } from '@angular/router';
import { select } from '@angular-redux/store';
import { Config } from '../../store/config.reducer';

@Injectable()
export class DashboardCanActivateService implements CanActivate {

  @select(['config']) config$;
  config: Config;

  constructor(
    private router: Router,
  ) {
    this.config$.subscribe((cfg: Config) => this.config = cfg);
  }

  // I determine if the requested route can be activated (ie, navigated to).
  public canActivate(activatedRouteSnapshot: ActivatedRouteSnapshot): boolean {

    if (this.config.param && this.config.param['p.dashboard.adt.enabledfor'] && this.config.param['p.dashboard.adt.enabledfor'].value) {
      this.router.navigate(['/dashboard/adt']);
    }
    return true;
  }
}
