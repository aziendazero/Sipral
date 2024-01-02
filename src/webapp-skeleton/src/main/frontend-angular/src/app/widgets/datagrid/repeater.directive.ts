import { Directive, TemplateRef, ViewContainerRef, Input } from '@angular/core';
@Directive({
  selector: '[repeat]'
})
export class RepeaterDirective {

  constructor(
    private template: TemplateRef<any>,
    private view: ViewContainerRef
  ) {
    // 1. DOM is hidden by default into a <template> element
    // 2. ... Add some logic here ...
    // 3. Render DOM by using following line
    // this.view.createEmbeddedView(this.template);
  }

  // Invoked each time the property or the template changes
  @Input() set repeatOf(list) {     /* [SELECTOR] + [OPERATOR] = repeater + Of */

    // clean current view
    this.view.clear();

    if (list != null) {
      for (let i = 0; i < list.length; i++) {
        this.view.createEmbeddedView(this.template, {
          $implicit: list[i]
          // $implicit: { // implicit va dentro all item
          //   $item: list[i] // ,
          //   // $index: i + 1
          // },
          // $total: list.length // questo va messo dopo es: let u of users; let t = total
        });

      }
    }
  }

}


// da usere:
// <div *repeat="let u of users" (click)="showProfile(u)">
// <h2>{{u.$index}} {{u.$displayName}} - {{u.$item.id}}</h2>
// </div>

// Cerca angular2 for microsintax
// https://angular.io/docs/ts/latest/guide/structural-directives.html#!#microsyntax
