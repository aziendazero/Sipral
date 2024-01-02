import { Component, Input } from '@angular/core';
import { Location } from '@angular/common';
import { select } from '@angular-redux/store';
import { PatientEncounterActionService } from '../../services/actions';
import { AdtAction } from './actions/adt-action.service';
import { ConversationActions } from '../../actions/conversation.actions';
import { PatientEncounter } from '../../services/entities/act';


@Component({
  template: `    
    <h1>{{'DcAdtNote' | translate}}</h1>
    <div class="layout vertical">
      <phi-textarea rows="auto" cols="50" [(ngModel)]="PatientEncounter.details" class="note" maxlength="2500"></phi-textarea>
      <div class="layout horizontal">
        <button type="button" (click)="saveNote()">Ok</button>
        <button type="button" (click)="close()">Cancel</button>
      </div>
    </div>
  `,
  styles: [`
    .note {
      display:block;
      margin-bottom: 10px;
    }
  `]
})
export class AdtNoteComponent {

  @select(['conversation']) conversation$;

  PatientEncounter: PatientEncounter;

  title: String = 'Nota ricovero';

  constructor(private ConversationActions: ConversationActions,
              private AdtAction: AdtAction,
              private PatientEncounterAction: PatientEncounterActionService,
              private location: Location) {

    this.conversation$.subscribe(res => {
      this.PatientEncounter = res.PatientEncounter;
    });

  }

  saveNote() {

    this.PatientEncounterAction.entity = this.PatientEncounter; // this.data;

    this.PatientEncounterAction.update()
      .then((PatientEncounter) => {
        this.ConversationActions.put('Adt', PatientEncounter);
        this.AdtAction.refresh();

        this.close();
      });
  }

  close(){
    this.location.back();
  }
}
