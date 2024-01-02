import { NgModule } from '@angular/core';

import { LOGIN_COMPONENTS } from './index';
import { LoaderModule } from '../components/loader.module';
import { SharedModule } from '../../shared/shared.module';
import { LoginRouting } from './login-routing.module';
import { LoginCanActivateService } from './login.can-activate.service';

@NgModule({
  declarations: [ ...LOGIN_COMPONENTS ],
  exports: [ ...LOGIN_COMPONENTS ],
  imports: [
    SharedModule,
    LoaderModule,
    LoginRouting
  ],
  providers: [ LoginCanActivateService ]   // <== wrong way. We should use forRoot()
})
export class LoginModule {}

