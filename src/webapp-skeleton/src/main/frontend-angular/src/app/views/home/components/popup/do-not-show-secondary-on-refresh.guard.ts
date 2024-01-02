import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router } from "@angular/router";

@Injectable()
export class DoNotShowSecondaryOnRefreshGuard implements CanActivate {

  private router: Router;

  constructor( router: Router ) {
    this.router = router;
  }

  // I determine if the requested route can be activated (ie, navigated to).
  public canActivate(activatedRouteSnapshot: ActivatedRouteSnapshot) : boolean {

    // We don't want to render this secondary view on page-refresh. As such, if this
    // is a page-refresh, we'll navigate to the same URL less the secondary outlet.
    if ( this.isPageRefresh() ) {
      this.router.navigate([{outlets: {popup: null}}], {queryParamsHandling: 'preserve'});
      return( false );
    }
    return true;
  }

  // I determine if the current route-request is part of a page refresh.
  private isPageRefresh(): boolean {

    // If the router has yet to establish a single navigation, it means that this
    // navigation is the first attempt to reconcile the application state with the
    // URL state. Meaning, this is a page refresh.
    return( ! this.router.navigated );

  }

}
