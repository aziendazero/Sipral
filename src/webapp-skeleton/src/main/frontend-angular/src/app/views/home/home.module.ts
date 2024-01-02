import { NgModule } from '@angular/core';
import { HomeRoutingModule } from './home-routing.module';
import { AuthGuard, DataService, DictionaryManager, HttpService, ParameterManager, ProcessManager, ViewManager } from '../../services';
import { DashboardModule } from '../../dashboard/dashboard.module';
import { ProcessActions } from '../../actions/process.actions';
import { ConversationActions } from '../../actions/conversation.actions';
import { ConfigActions } from '../../actions/config.action';
import { BannerActions } from '../../actions/banner.action';
import { HOME_COMPONENTS } from './index';
import { DIRECTIVES } from './index';
import { SharedModule } from '../../shared/shared.module';
import { ConversationResolve } from './conversation.resolve';
import { HomeResolve } from './home.resolve';
import { HomeCanActivateService } from './home.can-activate.service';
import { DoNotShowSecondaryOnRefreshGuard } from './components/popup/do-not-show-secondary-on-refresh.guard';
import { JsfContainerModule } from '../../pageContainer/jsf-container.module';
import { DashboardCanActivateService } from './dashboard.can-activate.service';
import { PkNetService } from '../../services/sign/pk-net.service';
import { CalamaioService } from 'app/services/sign/calamaio.service';


@NgModule({
  declarations: [...HOME_COMPONENTS, ...DIRECTIVES],
  exports: [...HOME_COMPONENTS, ...DIRECTIVES],
  entryComponents: [...HOME_COMPONENTS],
  imports: [
    SharedModule,
    HomeRoutingModule,
    DashboardModule,
    JsfContainerModule // Remove to load in chunk
  ],
  providers: [
    AuthGuard,
    DictionaryManager,
    HttpService,
    ParameterManager,
    ProcessManager,
    ViewManager,
    PkNetService,
    CalamaioService,
    DataService,

    ConversationResolve,
    HomeResolve,
    HomeCanActivateService,
    DashboardCanActivateService,
    DoNotShowSecondaryOnRefreshGuard,

    // redux actions
    ProcessActions,
    ConversationActions,
    ConfigActions,
    BannerActions
  ]
})
export class HomeModule { }
