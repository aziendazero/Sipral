import { NgModule } from '@angular/core';

import { JsfContainerComponent } from './jsf-container.component';
import { JsfContainerRouting } from './jsf-container.routing';
import { PhiTree } from '../legacy/tree';


@NgModule({
  declarations: [ JsfContainerComponent ],
  exports: [ JsfContainerComponent ],
  entryComponents: [],
  imports: [
   JsfContainerRouting
  ],
  providers: [
    PhiTree
  ]

})
export class JsfContainerModule {}

