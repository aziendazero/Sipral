/**
 * Created by Alex on 19/10/17.
 */
import { NgModule, LOCALE_ID }         from '@angular/core';
import { FormsModule }      from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TranslateModule }  from '@ngx-translate/core';

import { WidgetsModule }    from '../widgets/widgets.module';
import { DIRECTIVES, COMPONENTS } from '../widgets/index';
import { DateFormatPipe } from '../services/converters/date-format.pipe';

@NgModule({
  imports:      [
    CommonModule,
    WidgetsModule
  ],
  declarations: [ DateFormatPipe ],
  exports:      [ CommonModule, FormsModule, WidgetsModule, DateFormatPipe, ...COMPONENTS, ...DIRECTIVES, TranslateModule ],
  providers: [
    { provide: LOCALE_ID, useValue: "it-IT" },
    DateFormatPipe
  ]
})
export class SharedModule { }
