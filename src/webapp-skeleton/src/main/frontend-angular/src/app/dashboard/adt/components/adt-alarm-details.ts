import { Location } from '@angular/common';
import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
styleUrls: ['../adt.component.scss'],
templateUrl: 'adt-alarm-details.html'
})


export class AdtAlarmDetails extends BaseForm  {
  @select(['conversation', 'TherapyAlarmList']) TherapyAlarmList$;
  TherapyAlarmList;
  @select(['conversation', 'TherapyAlarm']) TherapyAlarm$;
  TherapyAlarm;

  constructor(injector: Injector,
    private location: Location) {
    super(injector,);
    this.TherapyAlarmList$.subscribe(res => this.TherapyAlarmList = res);
    this.TherapyAlarm$.subscribe(res => this.TherapyAlarm = res);
  }

  therapyAllarmiIcon(data) {

    if(data!==null && data !== undefined  ){
          if (data===0 ) {
            return 'grey';
          }else if(data===1){
            return 'green';
          }
          else if(data===5){
            return 'yellow orangeStroke';
          }
          else if(data===10){
            return 'red';
          }
    }
    return '';
  }
  therapyModuloDesc(data) {

    if(data!==null && data !== undefined  ){
          if (data=='TH' ) {
            return 'Terapie';
          }else if(data=='ACT'){
            return 'Attività';
          }

    }
    return '';
  }
  therapyTitleDesc(data) {

    if(data!==null && data !== undefined  ){
          if (data===0 ) {
            return 'nessuna attività presente';
          }else if(data===1){
            return 'tutte le attività sono state eseguite';
          }
          else if(data===5){
            return 'sono presenti attività da eseguire';
          }
          else if(data===10){
            return 'sono presenti attività non eseguite, in ritardo rispetto alla loro pianificazione';
          }
    }
    return '';
  }
  therapyAmbitoDesc(data) {

    if(data!==null && data !== undefined  ){
          if (data=='C' ) {
            return 'clinico';
          }else if(data=='N'){
            return 'infermieristico';
          }
          else if(data=='A'){
            return 'assistenziale';
          }
    }
    return '';
  }

  close(){
    this.location.back();
  }

}
