import { PrimaVitalSign } from './../entities/base-entity/prima-vital-sign';
import { BaseActionService } from './base-action.service';
import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { PatientEncounter } from '../entities/act';
import { reviver } from '../converters/any.converter';

@Injectable()
export class PrimaVitalSignActionService extends BaseActionService<PrimaVitalSign> {

    private static BY_SDL = '/bySdl';

    @select(['conversation', 'PatientEncounter']) patientEncounter$;
    patientEncounter: PatientEncounter;

    constructor(injectorz: Injector) {
        super(injectorz);
        this.entityName = 'PrimaVitalSignAction';
        this.entityUrl = 'primavitalsign';

        this.patientEncounter$.subscribe((pe) => {
            this.patientEncounter = pe;
        });
    }

    public countAlertedPrimaVitalSign(): Promise<boolean> {
        return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl +
            PrimaVitalSignActionService.BY_SDL + ';jsessionid=' + this.sid,
            {
                method: 'GET',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include'
            }
        )
        .then(response => response.text())
        .then(text => JSON.parse(text, reviver))
        .then(entity => {
            // console.log(entity);
            return Promise.resolve(entity.length > 0);
        }).catch((error) => {
            console.log(error.message);
            return Promise.resolve(false);
        });
    }
}
