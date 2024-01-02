import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DictionaryManager } from '../../services';
import { CodeValue } from '../../services/entities/data-types/code-value';
import { ServiceDeliveryLocationActionService } from '../../services/actions/service-delivery-location-action.service';
import { ProcessManager } from '../../services/process-manager';

@Component({
  selector: '[phi-node]',
  template: `
  <a class ="iconButton" *ngIf="hasChildren(); else leaf" (click)="onPlusClick()"><i class="fa fa-plus"></i></a>
  <ng-template #leaf>
    <i class="fa fa-leaf"></i>
  </ng-template>
  <input type="checkbox" *ngIf="checkbox && (!selectOnlyLeaves || selectOnlyLeaves && !hasChildren())" [checked]="value?.indexOf(item) !== -1" (click)="toggle()"/>
  <span [class.selected]="value == item" (click)="toggle()">{{getLabel()}}</span>
  <ul *ngIf="isExpanded() && listElementsExpression">
    <li phi-node *ngFor="let item of listElementsExpression;" [item]="item" [value]="value" [onSelect]="onSelect" [checkbox]="checkbox" [showCode]="showCode" (childToggled)="cascadeUp($event)" [hide]="hide" [expand]="expand" [level]="level+1">
    </li>
  </ul>
  `,
  styles: ['li {list-style: none;}' +
  '.selected {background-color: #ddd;}' +
  'span {cursor: pointer;}']
})
export class NodeComponent {
  @Input() item: any;
  @Input() value: any;
  @Input() public onSelect: Function;
  @Input() listElementsExpression: Array<any>;
  @Input() checkbox = false;
  @Input() selectOnlyLeaves = false;
  @Input() showCode = false;

  // Hide CodeValues if hide number equal to current level of nesting and show its children
  @Input() hide: number = null;

  // Expand tree if expand number lower to current level of nesting
  @Input() expand: number = null;

  // Current level of nesting
  @Input() level: number = null;

  @Output() childToggled: EventEmitter<NodeComponent> = new EventEmitter<NodeComponent>();

  _isExpanded = false;

  constructor(private dictionaryManager: DictionaryManager,
              private processManager: ProcessManager,
              private serviceDeliveryLocationAction: ServiceDeliveryLocationActionService) { }

  hasChildren() {
    if (this.item.children === undefined) { // CodeValue tree
      return this.item.type !== 'C';
    } else { // Login tree
      return this.item.children;
    }
  }

  isExpanded() {
    if (this.expand) {
      if (this.level < this.expand) {
        this.onPlusClick();
        return true;
      } else {
        return this._isExpanded;
      }
    } else {
      return this._isExpanded;
    }
  }

  onPlusClick() {

    if (!this._isExpanded) {
      if (this.item.children) {
        if (this.item.children instanceof Array) {
          this.listElementsExpression = this.item.children;
        } else if(this.item.children === true) {
          // Lazy loading of ProcSecurity or Dictionary or CodeValue: replacing jsTree
          if (this.item.path) { // ProcSecurity
            this.processManager.processsecurity(this.item.id).then((procSecurity: Array<any>) => {
              this.listElementsExpression = procSecurity;
            });
          } else {
            if (isNaN(this.item.id)) {
              //Load dictionary tree
              this.dictionaryManager.loadTree(null, null, this.item.id,'translation', 1, 'CodeValueParameter').then(cvTree => {
                this.listElementsExpression = cvTree;
              });

            } else {
              //Load sdloc tree
              this.serviceDeliveryLocationAction.loadTree('hbsManager', this.item.id).then((hbsTree: Array<any>) => {
                this.listElementsExpression = hbsTree;
              });
            }
          }
        }
        this._isExpanded = true;
      } else {
        if (this.item.displayName) { // Code Value
          this.dictionaryManager.get('NANDA:' + this.item.displayName, true) // FIXME
            .then((domain) => {

              const domainToLoad = [];
              if (this.hide && this.hide === this.level + 1) {
                // If hide is defined and equal to next level of nesting
                domain.map((cv: CodeValue) => {
                  domainToLoad.push(this.dictionaryManager.get('NANDA:' + cv.displayName, true));
                });
              }

              if (domainToLoad.length > 0) {
                // Hide level, replace with its children
                domain = [];
                Promise.all(domainToLoad).then((subDomains: Array<Array<CodeValue>>) => {
                  // When all children loaded
                  subDomains.map((sub: Array<CodeValue>) => {
                    domain = domain.concat(sub);
                  });
                  this.listElementsExpression = domain;
                  this._isExpanded = true;
                });
              } else {
                this.listElementsExpression = domain;
                this._isExpanded = true;
              }
            });
        }
      }
    } else {
      this._isExpanded = false;
    }
  }

  toggle() {
    let selected = this.onSelect(this.item);

    if(selected === false) {
      // If tree can select only leafs then expand node also clicking on text
      this.onPlusClick();
    }

    if (this.checkbox) {
      this.childToggled.emit(this);
    }
  }

  getLabel() {
    let label;
    if (this.showCode) {
      label = this.item.code + ' ' + this.item.currentTranslation;
    } else {
      label = this.item.currentTranslation || this.item.text;
    }
    return label;
  }

  cascadeUp(node: NodeComponent) {
    if (!this.selectOnlyLeaves) {
      if (!(this.value.indexOf(this.item) !== -1)) { // .includes(this.item)) {
        this.value.push(this.item);
      }
      this.childToggled.emit(this);
    }
  }
}
