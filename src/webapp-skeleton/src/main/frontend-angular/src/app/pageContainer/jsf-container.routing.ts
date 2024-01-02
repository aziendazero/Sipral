import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { JsfContainerComponent } from './jsf-container.component';

const routes: Routes = [
  { path: '', component: JsfContainerComponent }
];

export const JsfContainerRouting: ModuleWithProviders = RouterModule.forChild(routes);
