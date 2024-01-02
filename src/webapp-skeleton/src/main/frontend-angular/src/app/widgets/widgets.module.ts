import { NgModule } from '@angular/core';
import { CommonModule }  from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { COMPONENTS } from './index';
import { DIRECTIVES } from './index';

@NgModule({
  declarations: [ ...COMPONENTS, ...DIRECTIVES ],
  exports: [ ...COMPONENTS, ...DIRECTIVES ],
  entryComponents: [ ...COMPONENTS ],
  imports: [
    CommonModule,
    TranslateModule
  ],
  providers: []
})
export class WidgetsModule {}
