import {Component, Input, Output, OnChanges, SimpleChanges, EventEmitter, HostBinding} from '@angular/core';
import { ConversationActions } from '../actions/conversation.actions';

@Component({
    selector: '[phi-tabbedpanel]',
    template: `
    <ul class="tab-nav" *ngIf="(!hideSigleTab && binding != null) || (hideSigleTab && binding?.length > 1)">
        <li *ngFor="let entity of binding; let i = index">
          <a class="button {{getIcon(entity)}}" [class.activetab]="selectedTabIndex == i" (click)="ie(entity, i)">
            {{getLabel(entity)}}
          </a>
        </li>
    </ul>
    <ng-content></ng-content>
  `,

styleUrls: ['./tabbed-panel.component.scss']
})
export class TabbedPanelComponent implements OnChanges {

  @HostBinding('class') clazz = 'phi-tabbedpanel';

  // Array of BaseEntities
  @Input() binding: Array<any> = null;
  @Input() conversationName: string = null;

  // If true and binding length === 1 then hide tab
  @Input() hideSigleTab = false;
  @Input() styleClass: string;

  @Output() tabChange: EventEmitter<any> = new EventEmitter<any>();

  public selectedTabIndex = 0;

  // @Output() selected = new EventEmitter();

  constructor(private conversationActions: ConversationActions) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.binding && this.conversationName) {
      if (this.binding.length === 0) {
        this.conversationActions.remove(this.conversationName);
        this.selectedTabIndex = 0;
      } else {
        if (this.binding.length < this.selectedTabIndex + 1) {
          this.selectedTabIndex = 0;
        }
        this.ie(this.binding[this.selectedTabIndex], this.selectedTabIndex);
      }
    }
  }

  ie(entity, i)/*: Promise<any>*/ {
    this.selectedTabIndex = i;

    if (entity && this.conversationName) {
      // this.conversation[this.conversationName] = entity;
      this.conversationActions.put(this.conversationName, entity);
    //   this.BaseAction.entityName = this.conversationName;
    //   return this.BaseAction.inject(entity.internalId);
    }
    // this.selected.emit({selectedTab: i});
    this.tabChange.emit(this.selectedTabIndex);
  }

  getLabel(entity) { // FIXME REMOVE AND ADD TO TABED PANEL LABEL EL
    if (!entity) {
      return '';
    }
    if (entity.prescriptionMedicine && entity.prescriptionMedicine[0] && entity.prescriptionMedicine[0].medicine && entity.code.code === 'PHARMA') {
      return entity.prescriptionMedicine[0].medicine.name.giv;
    }
    if (entity.title) {
      return entity.title;
    }
    if (typeof entity === 'string' || entity instanceof String) {
      return entity;
    }
    return '-';
  }

  getIcon(entity) {
    if (entity.subTypeCode) {
      return entity.subTypeCode.code;
    }
  }

}
