/**
 * Created by Daniele on 08/09/2017.
 */

import {Directive, ElementRef, HostListener, Input} from "@angular/core";

@Directive({
  selector: '[textAreaRows]',
  host: {
    '(input)': 'setHeight()'
  }
})
export class TextAreaRowsDirective {

  @Input() textAreaRows: String;
  AUTO = "auto";

  constructor(public element: ElementRef) {
  }

  @HostListener('input', ['$event.target'])
  onInput(textArea: HTMLTextAreaElement): void {
    this.adjust();
  }

  ngAfterContentChecked(): void {
    if (this.textAreaRows === this.AUTO) {
      this.adjust();
    }
  }

  adjust(): void {
    let nativeElement = this.element.nativeElement;
    nativeElement.style.overflow = 'hidden';
    nativeElement.style.height = 'auto';
    nativeElement.style.height = nativeElement.scrollHeight + "px";
  }
}
