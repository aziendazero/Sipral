import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { PatientEncounter, Nanda } from '../entities/act';
import { Employee } from '../entities/role';
import { Config } from '../../store/config.reducer';
import { reviver } from '../converters/any.converter';

@Injectable()
export class NandaActionService extends BaseActionService<Nanda> {

  protected static NANDA_FACTORS = '/nandaFactors';
  protected static FAVORITE_NANDA = '/favoriteNanda';
  protected static SUGGESTED_NANDA_AND_RESOURCES = '/suggestedNandaAndResources';

  @select(['config']) config$;

  employee: Employee;
  employeeRoleCode: string;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Nanda';
    this.entityUrl = 'nandas';

    this.config$.subscribe( (cfg: Config) => {
      this.employee = cfg.employee;
      this.employeeRoleCode = cfg.employeeRoleCode;
    });
  }

  /**
   * Inject and replace patientEncounter with proxy
   */
  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<Nanda> {
    return super.inject(id, additionals, loads, conversationName).then( () => {
      if (this.entity && this.entity.patientEncounter) {
        this.entity.patientEncounter = {
          internalId: this.entity.patientEncounter.internalId,
          entityName: 'com.phi.entities.act.PatientEncounter'
        }
      }
      return this.entity;
    });
  }

  /**
   * Get NANDAEtiologicFactors, NANDASymptoms and NANDARiskFactors di CodeVlalueNanada.id
   */
  getNANDAFactors(nandaId: string): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + NandaActionService.NANDA_FACTORS + '/' + nandaId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        this.conversationActions.put('NANDAEtiologicFactors', entity.etiologicFactors);
        this.conversationActions.put('NANDASymptoms', entity.symptoms);
        this.conversationActions.put('NANDARiskFactors', entity.riskFactors);
        return entity;
      })
      .catch(error => {
        console.error('Error getNANDAFactors ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Get favorite diagnosis by ward sdl.id
   */
  getFavoriteNanda(wardId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + NandaActionService.FAVORITE_NANDA + '/' + wardId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        this.conversationActions.put('FavoriteNandaList', entity);
        return entity;
      })
      .catch(error => {
        console.error('Error getNANDAFactors ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Get suggested diagnosis by encounterId
   */
  getSuggestedNandaAndCheckingResources(encounterId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + NandaActionService.SUGGESTED_NANDA_AND_RESOURCES + '/' + encounterId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        this.conversationActions.put('SuggestedNandaList', entity.suggestedNanda);
        if (entity.checkingResources) {
          entity.checkingResources = entity.checkingResources.map((r) => {return {value: r, label: r}});
        }
        this.conversationActions.put('CheckingResources', entity.checkingResources);
        return entity.suggestedNanda;
      })
      .catch(error => {
        console.error('Error getNANDAFactors ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Create a new nanda diagnosis
   * @returns {Nanda}
   */
  public newNanda(patientEncounter: PatientEncounter, diagTot: number, coded = false): Nanda {
    this.entity = {};

    this.entity.actNandaDate = new Date();
    this.entity.typePCP = { codeSystemName: 'PHIDIC', domainName: 'NandaTypePCP', code: 'P'};
    this.entity.progNumber = diagTot + 1;
    this.entity.riskCode = {codeSystemName: "PHIDIC", domainName: "RiskValue", code: "NOTATRISK"};
    this.entity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };
    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    this.entity.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };
    if (coded) {
      this.entity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'NandaType', code: 'NANDACODED'};
    } else {
      this.entity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'NandaType', code: 'NANDANOTCODED'};
    }

    this.conversationActions.put(this._entityName, this.entity);

    return this.entity;
  }

  cancel(): Promise<Nanda> {

    this.entity.cancellationDate = new Date();
    // this.entity.cancellationNote = deleteNote;

    this.entity.cancelledBy = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    this.entity.cancelledByRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    if (this.entity.patientEncounter) {
      this.entity.patientEncounter = {
        internalId: this.entity.patientEncounter.internalId,
        entityName: 'com.phi.entities.act.PatientEncounter'
      };
    }

    return this.update();
  }

}
