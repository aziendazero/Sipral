import { Employee } from '../../../../../services/entities/role/employee';
import { Component } from '@angular/core';
import { select } from '@angular-redux/store';
import { Patient } from '../../../../../services/entities/role/patient';
import { PatientEncounter } from '../../../../../services/entities/act/patient-encounter';
import { DictionaryManager } from '../../../../../services/dictionary-manager';
import { CodeValue } from '../../../../../services/entities/data-types/code-value';
import { environment } from 'environments/environment';

@Component({
  selector: 'phi-banner-details',
  templateUrl: './banner-details.component.html',
  styleUrls: ['./banner-details.component.scss']
})
export class BannerDetailsComponent {

  @select(['conversation', 'Patient']) Patient$;
  Patient: Patient;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;
  @select(['conversation', 'BannerAdmitter']) BannerAdmitter$;
  BannerAdmitter: Employee;
  @select(['conversation', 'BannerReferrer']) BannerReferrer$;
  BannerReferrer: Employee;
  @select(['banner']) Banner$;
  Banner;
  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;
  environment = environment;
  gender;

  constructor(private dictionaryManager: DictionaryManager) {

    this.Patient$.subscribe((res) => {
      this.Patient = res;
      if (this.Patient && this.Patient.genderCode) {
        this.dictionaryManager.getById(this.Patient.genderCode.id).then((cv: CodeValue) => this.gender = cv.currentTranslation);
      }
    });
    this.PatientEncounter$.subscribe((res) => {this.PatientEncounter = res;});
    this.BannerAdmitter$.subscribe((res) => this.BannerAdmitter = res);
    this.BannerReferrer$.subscribe((res) => this.BannerReferrer = res);
    this.Banner$.subscribe((res) => this.Banner = res);
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

}
