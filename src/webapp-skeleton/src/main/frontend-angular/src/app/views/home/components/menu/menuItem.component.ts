import { Component, Input } from '@angular/core';

@Component({
  selector: 'phi-menu-item',
  templateUrl: './menuItem.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuItemComponent {

  public title;
  public path;
  public _process;

  @Input() set process(process: any) {
    this._process = process;
    if (process.type) { // if type is present proc security is on db, loaded by processmanagerRest
      this.title = process.text;
      if ('p' === process.type) {
        this.path = ['/process', process.path];
      } else if ('d' === process.type) {
        this.path = [process.path];
        if ('ci' === process.area) {
          this.iconClass = 'fa-bed';
        } else {
          this.iconClass = 'fa-user-md';
        }
      } else if ('f' === process.type) {
        this.iconClass = 'fa-chevron-right';
      }
    } else if (process.leaf !== undefined) { // if proc security isnt on db tree is loaded by treebean
      this.title = process.title;
      this.path = ['/process', process.path];
      if (!process.leaf) {
        this.iconClass = 'fa-chevron-right';
      }
    }
  };
  @Input() public onProcessClick: () => any;
  navClass = 'nav';
  iconClass;

  onFolderClick() {
    // console.log('folder click');
    if (this.navClass === 'nav') {
      this.navClass = 'nav visible';
      this.iconClass = 'fa-chevron-down';
    } else {
      this.navClass = 'nav';
      this.iconClass = 'fa-chevron-right';
    }
  }
}
