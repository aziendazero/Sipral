import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';

import { NgReduxModule, NgRedux, DevToolsExtension } from '@angular-redux/store';
import { IAppState, rootReducer } from './store/index';

import { AppComponent } from './app.component';
import { HomeModule } from './views/home/home.module';
import { LoginModule } from './views/login/login.module';

import { AppRoutingModule } from './app-routing.module';

import { TranslateModule, TranslateLoader} from '@ngx-translate/core';
import { ProperyLoader } from './services/translate/property-loader';
import { environment } from '../environments/environment';
import { GlobalErrorHandler } from './services/error/global-error-handler';
import { LoaderModule } from './views/components/loader.module';

export const API_URL = 'apiUrl';

// AoT requires an exported function for factories
export function ProperyLoaderFactory() {
  return new ProperyLoader(environment.apiUrl);
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    LoginModule,
    HomeModule,
    AppRoutingModule,
    NgReduxModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: ProperyLoaderFactory
      }
    }),
    LoaderModule
  ],
  exports: [
    TranslateModule
  ],
  providers: [
     {
      provide: API_URL,
      useValue: environment.apiUrl
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    },
    GlobalErrorHandler
  ],
  entryComponents: [],
  bootstrap: [AppComponent]
})

export class AppModule {
  constructor(ngRedux: NgRedux<IAppState>, private devTool: DevToolsExtension){
    // Reducer, Initial State, Opts MiddleWares, Opts Enhancers
    ngRedux.configureStore(rootReducer, {} as IAppState, [],
      [ devTool.isEnabled() ? devTool.enhancer() : f => f]
    );
  }
}
