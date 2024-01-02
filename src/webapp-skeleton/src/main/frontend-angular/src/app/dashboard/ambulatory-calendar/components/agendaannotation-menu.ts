import { AgendaAnnotationActionService } from './../../../services/actions/agenda-annotation-action.service';
import { Component, Injector } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BaseForm } from '../../../widgets/form/base-form';
import { ViewManager } from '../../../services/view-manager';
import { AmbulatoryPortal } from '../../ambulatory-portal/ambulatory-portal';
import { AmbulatoryCalendar } from '../ambulatory-calendar';
import { AmbulatoryCalendarAction } from '../actions/ambulatory-calendar-action.service';
import { AgendaAnnotationEdit } from './agendaannotation-edit';

@Component({
  selector: 'phi-agendaannotation-menu',
  templateUrl: './agendaannotation-menu.html'
})
export class AgendaAnnotationMenu extends BaseForm {


  agendaAnnotationId: number;


  constructor(injector: Injector,
    private route: ActivatedRoute,
    private viewManager: ViewManager,
    public AmbulatoryCalendarAction: AmbulatoryCalendarAction,
    private AgendaAnnotationAction: AgendaAnnotationActionService,
  ) {
    super(injector);

    this.agendaAnnotationId = +this.route.snapshot.params['agendaannotation'];
  }

  edit() {

    this.viewManager.setPopupViewId('agendaannotation-edit', this.agendaAnnotationId.toString()).then((aae: AgendaAnnotationEdit) =>
      aae.save.subscribe(() =>
        this.refreshForm()
      )
    );
  }

  copy() {
    this.AmbulatoryCalendarAction.copyCalendarItem(this.agendaAnnotationId, 'Note');
    this.viewManager.setPopupViewId(null);
  }

  move() {
    this.AmbulatoryCalendarAction.moveCalendarItem(this.agendaAnnotationId, 'Note');
    this.viewManager.setPopupViewId(null);
  }



  delete() {
    this.AgendaAnnotationAction.entity = { internalId: this.agendaAnnotationId };
    this.AgendaAnnotationAction.delete().then(() => {
      this.viewManager.setPopupViewId(null);
      this.refreshForm();
    });
  }


  refreshForm() {
    if (this.viewManager.formComponent instanceof AmbulatoryPortal) {
      this.viewManager.formComponent.refresh();
    } else if (this.viewManager.formComponent instanceof AmbulatoryCalendar) {
      this.viewManager.formComponent.refresh(true);
    }
  }
}
