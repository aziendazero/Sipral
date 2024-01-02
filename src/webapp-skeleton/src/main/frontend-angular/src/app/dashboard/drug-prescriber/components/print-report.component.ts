import { Component, Injector, Input, OnInit } from '@angular/core';
import { select } from '@angular-redux/store';
import { DrugPrescriberAction } from './../actions/drug-prescriber-action.service';

import { PatientEncounter } from '../../../services/entities/act';
import { BaseForm } from '../../../widgets/form/base-form';

@Component({
  templateUrl: './print-report.html'
})
export class PrintReportComponent extends BaseForm implements OnInit {

  @select(['conversation']) conversation$;
  PatientEncounter: PatientEncounter;

  @Input() data: any;

  reportStartFromEnc = false;
  untilToday = true;

  constructor(injector: Injector,
              public DrugPrescriberAction: DrugPrescriberAction) {
    super(injector);

    this.conversation$.subscribe(res =>
      this.PatientEncounter = res.PatientEncounter
    );
  }

  ngOnInit() {
    const now = new Date();
    const nowminus3Days = new Date(now);
    nowminus3Days.setDate(nowminus3Days.getDate() - 3);
    this.DrugPrescriberAction.equal['startDate'] = nowminus3Days;
    this.DrugPrescriberAction.equal['endDate'] = now;
  }

  startFromEncChanged() {
    if (this.reportStartFromEnc) {
      this.DrugPrescriberAction.equal['startDate'] = this.PatientEncounter.availabilityTime;
    } else {
      this.DrugPrescriberAction.equal['startDate'] = new Date();
    }
  }

  untilTodayChanged() {
    if (this.untilToday) {
      this.DrugPrescriberAction.equal['endDate']  = new Date();
    }
  }

  printReport() {
    if (this.PatientEncounter.therapy != null && this.PatientEncounter.therapy.length > 0) {
      this.DrugPrescriberAction.equal['therapyId'] = this.PatientEncounter.therapy[0].internalId;
      this.DrugPrescriberAction.printReport("swf/modules/DrugPrescriber/reports/r_drugPrescription.seam");
    }
  }

}
