import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateChild } from '@angular/router';
import { select } from '@angular-redux/store';
import { Config } from '../../store/config.reducer';
import { AuthGuard } from '../../services/auth-guard.service';

@Injectable()
export class HomeCanActivateService implements CanActivateChild {

  @select(['config']) config$;
  config: Config;

  constructor(
    private router: Router,
    private authGuard: AuthGuard
  ) {
    this.config$.subscribe( (cfg: Config) => this.config = cfg);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {
    // console.log('HOME ROUTER [canActivateChild]: Trying to route to ' + route.url[0].path);

    return this.authGuard.isAuthenticated().then((auth) => {
      if (auth && !this.config.passwordExpired && !this.config.options) {
        return true;
      } else {
        this.router.navigate(['/login']);
        return false;
      }
    });

  }
}
