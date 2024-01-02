import { BannerActions } from './../../../../../actions/banner.action';
import { Patient } from './../../../../../services/entities/role/patient';
import { Config } from './../../../../../store/config.reducer';
import { AnnotationActionService } from './../../../../../services/actions/annotation-action.service';
import { ConversationActions } from './../../../../../actions/conversation.actions';
import { Annotation } from './../../../../../services/entities/act/annotation';
import { Employee } from '../../../../../services/entities/role/employee';
import { Component } from '@angular/core';
import { select } from '@angular-redux/store';
import { Location } from '@angular/common';
@Component({
  selector: 'phi-patient-note',
 // templateUrl: './patient-note.component.html',
//styleUrls: ['./patient-note.component.scss'],
  template: `    
    <h3>{{'DcAdtNote' | translate}}</h3>
    <div class="layout vertical" class="notepp">
      <phi-textarea [(ngModel)]="PatientNote.text.string" class="note" maxlength="2200" [required]="true"></phi-textarea>
      <div class="layout horizontal">
        <button type="button"  (click)="saveNote()">Ok</button>
        <button type="button" (click)="close()">Cancel</button>
      </div>
    </div>
  `,
  styles: [`
  .notepp{
    width:600px;
  }
    .note {
      display:block;
      margin-bottom: 10px;
    }
  `]
})


export class PatientNoteComponent {
  @select(['banner']) Banner$;Banner;
  @select(['config']) config$;
  @select(['conversation', 'Patient']) patient$;
  patient: Patient;

  PatientNote: Annotation;
  employee: Employee;
  title: String = 'Nota Paziente';
 
  

    constructor(private ConversationActions: ConversationActions,
      private AnnotationAction: AnnotationActionService,
      private BannerAction: BannerActions,private location: Location) {
      this.patient$.subscribe((pat) => {
        this.patient = pat;
      });
      this.config$.subscribe( (cfg: Config) => {
        this.employee = cfg.employee;
      });
      this.Banner$.subscribe(
        (res) =>{
          this.Banner = res;
          this.PatientNote=this.Banner['PatientNote'];
          if (this.PatientNote) {
            if(!this.PatientNote.text) {
              this.PatientNote.text = {};
            }
            this.AnnotationAction.entity= this.PatientNote;
            this.PatientNote.bannerNote=true;
            this.PatientNote.isActive=true;
            this.PatientNote.availabilityTime=new Date();
            if(!this.PatientNote.patient){
              this.saveNote(true);
            }
          }
        }  
    );
    
}

saveNote(dontclose = false) {
    this.PatientNote.patient = this.patient;
    this.PatientNote.author = this.employee;

    this.AnnotationAction.update()
   .then(() => {
        if (!dontclose) {
          this.close();
        }
        this.PatientNote = this.AnnotationAction.entity;
  
        this.Banner.PatientNote = this.PatientNote;
        if(!this.PatientNote.text || !this.PatientNote.text.string){
          this.Banner.managementColor=null;
        } else {
          this.Banner.managementColor="green";
        }
        
        this.BannerAction.put(this.Banner);
    });
}

  
  
    close(){
      this.location.back();
    }
  
   

}

