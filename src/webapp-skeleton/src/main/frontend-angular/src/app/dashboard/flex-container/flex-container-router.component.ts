import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FlexContainerService } from './flex-container.service';
import {ProcessActions} from '../../actions/process.actions';

@Component({
  template: '<i></i>'
})

export class FlexContainerRouterComponent {

  constructor(
    private flexService: FlexContainerService,
    private router: Router,
    private route: ActivatedRoute,
    private processActions: ProcessActions
  ) {
    route.params.subscribe((val) => { //every time you fo to /flex/:dashboardName
      if (this.router.url.indexOf('flex/') !== -1) {
        this.processActions.setDashboard(this.router.url); // save the url to retry flexCommunicator if flex is not yet initialized: see flex-container.component.ts flexInit()
        let dashboardName = this.router.url.substr(this.router.url.indexOf('flex/') + 5,this.router.url.length);
        this.flexService.flexCommunicator(dashboardName, null, null);
      }
    });
  }
}
