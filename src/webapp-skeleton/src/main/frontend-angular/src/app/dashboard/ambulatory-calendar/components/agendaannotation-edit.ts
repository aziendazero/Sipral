import { Component, EventEmitter, Injector, Output } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { AgendaAnnotation } from '../../../services/entities/act';
import { ViewManager } from '../../../services/view-manager';
import { AgendaAnnotationActionService } from '../../../services/actions';
import { ActivatedRoute } from '../../../../../node_modules/@angular/router';


@Component({
  selector: 'phi-agendaannotation-edit',
  templateUrl: './agendaannotation-edit.html'
})

export class AgendaAnnotationEdit extends BaseForm {
  @select(['conversation', 'AgendaAnnotation']) AgendaAnnotation$;
  AgendaAnnotation: AgendaAnnotation;
  isAppointmentNote = false;

  @Output() save: EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
    private viewManager: ViewManager,
    private AgendaAnnotationAction: AgendaAnnotationActionService,
    private route: ActivatedRoute) {
    super(injector);
    this.AgendaAnnotation$.subscribe(res => {
      this.AgendaAnnotation = res;
      if (this.AgendaAnnotation && this.AgendaAnnotation.lengthCode) {
        this.isAppointmentNote = true;
      }
    });

    if (this.route.snapshot.params['agendaannotation'] === 'newemptyapp') {
      this.isAppointmentNote = true;
    }
  }

  onSave() {
    this.AgendaAnnotationAction.create().then(() => {
      this.viewManager.setPopupViewId(null);
      this.save.emit(this.AgendaAnnotation);
    });
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }

}

