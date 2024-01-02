import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LabReport } from './lab-report';

const routes: Routes = [
  { path: '', component: LabReport }
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
