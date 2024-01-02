import { Injectable, Inject } from '@angular/core';
import { Router, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, NavigationEnd } from '@angular/router';
import { select } from '@angular-redux/store';
import { Config } from '../../store/config.reducer';
import { AuthGuard } from '../../services/auth-guard.service';

@Injectable()
export class LoginCanActivateService implements CanActivateChild {

  @select(['config']) config$;
  config: Config;

  previousUrl;

  constructor(@Inject('apiUrl') private url, private router: Router, private authGuard: AuthGuard) {
    this.config$.subscribe( (cfg: Config) => this.config = cfg);

    this.router.events
      .filter(event => event instanceof NavigationEnd)
      .subscribe((e: NavigationEnd) => {
        this.previousUrl = e.url;
      });
  }

 canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {

   return this.authGuard.isAuthenticated().then(() => { // load user informations
     let destination = "";
     if (!this.config.employee) { // not logged in or (TODO) redux il lost beacuse the browser has been closed!
       destination = '/login/signin';
     } else if (this.config.passwordExpired) { // logged in but password expired
       destination = '/login/password';
     } else if (this.config.options) { // logged in, password valid, but not options selected
       destination = '/login/options';
     } else if (this.previousUrl) {
       destination = '/home';
     } else { // already logged in
        destination = '/login/alreadyLogged';
     }

     if (destination && destination.indexOf(route.url[0].path) === -1) { // you are in the wrong url!
       this.router.navigate([destination]);
       return false;
     } else { // ok stay here!
       return true;
     }
   });
 }

}
