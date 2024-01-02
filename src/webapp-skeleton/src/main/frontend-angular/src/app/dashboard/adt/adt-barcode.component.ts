import { Component } from '@angular/core';
import { select } from '@angular-redux/store';

import { PatientEncounterActionService } from '../../services/actions/patient-encounter-action.service';
import { Location } from '@angular/common';

@Component({
  template: `    
    <h1>Barcode</h1>
    <input type="text"(change)="searchBarcode($event)">
  `
})
export class AdtBarcodeComponent {

  @select(['conversation', 'PatientEncounterList']) PatientEncounterList$;
  PatientEncounterList;

  title: String = 'Search by barcode';

  constructor(private PatientEncounterAction: PatientEncounterActionService, private location: Location) {
    this.PatientEncounterList$.subscribe(res => this.PatientEncounterList = res);
  }

  searchBarcode(event) {
    let patientEncounterList: Array<any> = this.PatientEncounterList.entities;

    let encounterFound;
    let barcode: string;

    for (let i = 0; i < patientEncounterList.length; i++) {
      barcode = patientEncounterList[i].barcode;

      if (barcode !== null && barcode === event.srcElement.value) {
        encounterFound = patientEncounterList[i];
        break;
      }
    }

    if (encounterFound != null) {
      this.PatientEncounterAction.inject(encounterFound.internalid, null, ['therapy']).then(() =>
        this.location.back()
      );
    }
  }
}
