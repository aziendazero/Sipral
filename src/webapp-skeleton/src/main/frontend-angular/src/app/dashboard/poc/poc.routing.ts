import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { Poc } from './poc';

const routes: Routes = [
  { path: '', component: Poc }
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
