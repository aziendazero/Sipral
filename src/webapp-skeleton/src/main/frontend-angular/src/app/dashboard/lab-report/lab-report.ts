import { AfterViewInit, Component, ElementRef, Injector, NgZone, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';
import { PatientEncounter } from '../../services/entities/act';
import { LisAction } from './actions/lis-action.service';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ConversationActions } from '../../actions/conversation.actions';

@Component({
  selector: 'phi-lab-report',
  templateUrl: './lab-report.html',
  styleUrls: ['./lab-report.scss']
})
export class LabReport extends BaseForm implements AfterViewInit {

  @select(['conversation', 'PatientEncounter']) patientEncounter$;
  patientEncounterSub;
  PatientEncounter: PatientEncounter;

  @select(['conversation', 'DettaglioRefertoLabList']) dettaglioRefertoLabList$;
  dettaglioRefertoLabListSub;
  DettaglioRefertoLabList: Datamodel;

  @ViewChild('labreportexam')
  private labreportExam: ElementRef;

  @ViewChild('labreport')
  private labreport: ElementRef;

  startDate: Date;
  endDate: Date;

  from: Date;
  to: Date;

  constructor(injector: Injector,
              private router: Router,
              private conversationActions: ConversationActions,
              private LisAction: LisAction,
              private zone: NgZone) {
    super(injector);

    this.patientEncounterSub = this.patientEncounter$.subscribe(res => {
      this.PatientEncounter = res;
    });
    this.dettaglioRefertoLabListSub = this.dettaglioRefertoLabList$.subscribe(res => {
      this.DettaglioRefertoLabList = res;
    });

    let today: Date = new Date();

    let oneMonthsAgo : Date = new Date( today.getFullYear(), today.getMonth() - 1, today.getDate());
    let oneMonthsAnd5DaysAgo : Date = new Date( today.getFullYear(), today.getMonth() - 1, today.getDate() - 5);
    // let twentyDaysAgo : Date = new Date( today.getFullYear(), today.getMonth(), today.getDate()-20);
    let in5days : Date = new Date( today.getFullYear(), today.getMonth(), today.getDate() + 5);

    this.startDate = oneMonthsAnd5DaysAgo;// oneMonthsAgo;
    this.from = oneMonthsAgo; // twentyDaysAgo;
    this.to = today;
    this.endDate = in5days;
  }

  ngOnInit() {
    if (this.PatientEncounter) {
      this.refresh();
    } else {
      this.router.navigate(['/dashboard', 'adt']);
    }
  }

  ngAfterViewInit() {
    this.zone.runOutsideAngular(() => {
    if (this.labreportExam && this.labreport) {
      this.labreport.nativeElement.addEventListener('scroll', (ev: any) =>
        this.labreportExam.nativeElement.scrollTop = ev.target.scrollTop
      );
    }
    });
  }

  ngOnDestroy() {
    this.patientEncounterSub.unsubscribe();
    this.dettaglioRefertoLabListSub.unsubscribe();

    this.conversationActions.remove('DettaglioRefertoLabList');
  }

  refresh(ev = null) {

    if (ev && ev.highValue) {
      this.to = ev.highValue;
    }
    // console.log(this.from + ' - ' + this.to);

    this.LisAction.equal['encounterId'] = this.PatientEncounter.internalId;
    this.LisAction.equal['startDate'] = this.from;
    this.LisAction.equal['endDate'] = this.to;
    this.LisAction.equal['simpleResults'] = true;

    this.LisAction.init();
  }

  // onScroll(ev) {
  //   this.labreportExamDiv.scrollTop = ev.target.scrollTop;
  // }
}
