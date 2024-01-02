import { Annotation } from './../entities/act/annotation';
import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';

@Injectable()
export class AnnotationActionService extends BaseActionService<Annotation> {

  
  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'PatientNote';
  }

  }
