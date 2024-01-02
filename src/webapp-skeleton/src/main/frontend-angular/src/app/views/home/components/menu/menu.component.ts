import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { Subscription } from 'rxjs/Subscription';
import { PatientEncounter } from '../../../../services/entities/act/patient-encounter';
import { ConfigActions } from '../../../../actions/config.action';
import { environment } from '../../../../../environments/environment';
import { ProcessActions } from 'app/actions/process.actions';

export class Process {
  title: string;
  path: string;
}

@Component({
  selector: 'phi-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnDestroy {

  @select(['config' , 'menuVisible']) menuVisible$;
  menuVisible;
  @select(['config' , 'area']) area$;
  area;
  @select(['banner' , 'visible']) bannerVisible$;
  bannerVisible;
  @select(['process', 'definitions']) processList$;
  processListSub: Subscription;
  processList;
  processByAreaList;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;
  @select(['config', 'param']) param$;
  Param: Map<string, any>;
  @select(['process', 'version']) phiVersion$;
  phiVersion;

  constructor(
    private router: Router,
    public configActions: ConfigActions,
    private processActions: ProcessActions
    ) {

    this.menuVisible$.subscribe((res) => this.menuVisible = res);
    this.bannerVisible$.subscribe((res) => this.bannerVisible = res);
    this.area$.subscribe((res) => {
      this.area = res;
      this.filterProcessListByArea();
    });
    this.processListSub = this.processList$.subscribe((res) => {
      this.processList = res;
      if (this.processList) {
        this.processList.unshift(...environment.manu.processes)
      }
      this.filterProcessListByArea();
    });
    this.param$.subscribe(res => this.Param = res);

    this.phiVersion$.subscribe((res) => {
      this.phiVersion = res;
    });

    this.PatientEncounter$.subscribe((res) => {
      this.PatientEncounter = res;
    });
  }

  ngOnDestroy() {
    this.processListSub.unsubscribe();
  }

  /**
   * Filter process by area ci or amb
   */
  filterProcessListByArea() {

    let filter = (processList: Array <any>) => {
      return processList.filter((process) => {
        if (process.type === 'f' && process.children) {
          process.children = filter(process.children);
          if (process.children.length === 0) {
            return false;
          }
        }
        return !process.area || process.area === this.area;
      });
    };

    if (this.area && this.processList) {
      const clonedProcessList = JSON.parse(JSON.stringify(this.processList));
      this.processByAreaList = filter(clonedProcessList);
    } else {
      this.processByAreaList = this.processList;
    }
  }


  onClick = () => {
    this.processActions.setDashboard(this.router.url);
    this.configActions.toggleMenu();
  }

  showWard() {
    if (this.area !== 'ci') {
      this.configActions.put('area', 'ci');
    } else {
      this.configActions.remove('area');
    }
  }

  showAmbulatory() {
    if (this.area !== 'amb') {
      this.configActions.put('area', 'amb');
    } else {
      this.configActions.remove('area');
    }
  }

}
