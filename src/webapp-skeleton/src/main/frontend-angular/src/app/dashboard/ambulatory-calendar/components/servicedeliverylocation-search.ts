import { Component, EventEmitter, Injector, OnDestroy, Output } from '@angular/core';
import { Location } from '@angular/common';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ServiceDeliveryLocation } from '../../../services/entities/role/service-delivery-location';
import { AmbulatoryCalendarAction } from '../actions/ambulatory-calendar-action.service';

@Component({
selector: 'phi-servicedeliverylocation-search',
templateUrl: './servicedeliverylocation-search.html',
  styles: ['/deep/ .searchSdlocTree{ max-height: calc(100vh - 230px);overflow: auto;}']
})
export class ServicedeliverylocationSearch extends BaseForm implements OnDestroy {

  @select(['conversation', 'ServiceDeliveryLocationList']) serviceDeliveryLocationList$;
  serviceDeliveryLocationListSub;
  ServiceDeliveryLocationList: Datamodel;

  enabledSdlocs;

  sdLocs = []; // Selected sdlocs
  allSdLocs = [];

  name;

  @Output() select : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private location: Location,
              ambulatoryCalendarAction: AmbulatoryCalendarAction) {
    super(injector);

    this.serviceDeliveryLocationListSub = this.serviceDeliveryLocationList$.subscribe(res => {
      this.ServiceDeliveryLocationList = res;

      if (this.ServiceDeliveryLocationList) {
        const tf = this.toTreeDatamodel(this.ServiceDeliveryLocationList.entities);
        this.enabledSdlocs = tf.tree;
        this.allSdLocs = tf.flat;

        const enabledSdlId: Array<number> = ambulatoryCalendarAction.agendas.map((sdl: ServiceDeliveryLocation) => sdl.internalId);

        this.sdLocs = this.allSdLocs.filter((sdl:any) =>
          enabledSdlId.indexOf(sdl.id) !== -1 // contains
        );
      }
    });
  }

  ngOnDestroy() {
    this.serviceDeliveryLocationListSub.unsubscribe();
  }

  filter(value){
    // using value and not this.name since in ie11 this.name is not always filled...
    if (value) {
      value = value.toLowerCase();
      this.sdLocs = this.allSdLocs.filter((sdl) =>
        sdl.text.toLowerCase().indexOf(value) !== -1 // .includes(..))
      );
    } else {
      this.sdLocs = this.allSdLocs;
    }
  }

  selectAll() {
    this.sdLocs = this.allSdLocs;
  }

  unselectAll() {
    this.sdLocs = [];
  }

  onSelect() {
    const selectedIds = this.sdLocs.map((sdl) => +sdl.id);
    const selectedSdl = this.ServiceDeliveryLocationList.entities.filter((sdl: ServiceDeliveryLocation) => selectedIds.indexOf(sdl.internalId) !== -1 );
    this.select.emit(selectedSdl);
    this.location.back();
  }

  onCancel() {
    this.location.back();
  }

  private toTreeDatamodel(sdlocs: Array<ServiceDeliveryLocation>) {
    const results = {
      tree: [],
      flat: []
    };

    sdlocs.map((sdloc: ServiceDeliveryLocation) => {
      let addedParent = results.tree.find((parent) => parent.id === sdloc.parent.internalId);
      if (!addedParent) {
        addedParent = { text: sdloc.parent.name.giv, id: sdloc.parent.internalId, children: []};
        results.tree.push(addedParent);
      }
      const addedChild = { text: sdloc.name.giv, id: sdloc.internalId, children: false}
      addedParent.children.push(addedChild);
      results.flat.push(addedChild);
    });

    return results;
  }
}
