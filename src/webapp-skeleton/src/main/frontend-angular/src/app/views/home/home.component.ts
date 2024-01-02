import { Component, OnDestroy } from '@angular/core';
import { select } from '@angular-redux/store';
import { ConfigActions } from '../../actions/config.action';
import { BannerActions } from '../../actions/banner.action';
import { HttpService } from '../../services/http.service';
import { Router } from '@angular/router';
import { ViewManager } from '../../services/view-manager';
import { ConversationActions } from 'app/actions/conversation.actions';

@Component({
  selector: 'phi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})

export class HomeComponent implements OnDestroy {
  @select(['config' , 'menuVisible']) menuVisible$;   // use angular-redux select decorator
  menuVisibleSub;
  menuVisible;
  @select(['banner' , 'visible']) bannerVisible$;   // use angular-redux select decorator
  bannerVisibleSub;
  bannerVisible;
  @select(['conversation', 'selectedDate']) selectedDate$;
  selectedDateSub;
  selectedDate: Date;

  showDashboardAsPopup = false;
  btnToClickOnClose;

  selectedDateBackup;

  constructor(
    public ConfigActions: ConfigActions, // used in html for menuVisible
    public BannerActions: BannerActions, // used in html for bannerVisible
    public httpService: HttpService,
    public router: Router, // used in html for phi-flex-container visibility
    private viewManager: ViewManager,
    private conversationActions: ConversationActions
  ) {
    this.menuVisibleSub = this.menuVisible$.subscribe((res) => this.menuVisible = res);
    this.bannerVisibleSub = this.bannerVisible$.subscribe((res) => this.bannerVisible = res);
    this.selectedDateSub = this.selectedDate$.subscribe((res) => this.selectedDate = res);

    window['showDashboardAsPopup'] = (btnToClickOnClose, event) => {
       this.showDashboardAsPopup = true;
       this.selectedDateBackup = this.selectedDate;
       if (btnToClickOnClose) {
	       this.btnToClickOnClose = btnToClickOnClose;
	   }
    }
  }

  ngOnDestroy() {
    this.menuVisibleSub.unsubscribe();
    this.bannerVisibleSub.unsubscribe();
    this.selectedDateSub.unsubscribe();
  }

  formLoaded(componentRef) {
    // console.log('Form loaded ' + componentRef.constructor.name);
    this.viewManager.formComponent = componentRef;
  }

  formUnLoaded(componentRef) {
    // console.log('Form unloaded ' + componentRef.constructor.name);
    this.viewManager.formComponent = null;
  }

  hideDashboardPopup() {
    this.conversationActions.put('selectedDate', this.selectedDateBackup);
    this.showDashboardAsPopup = false;
    if (this.btnToClickOnClose) {
        this.btnToClickOnClose.click();
    }
  }
}

