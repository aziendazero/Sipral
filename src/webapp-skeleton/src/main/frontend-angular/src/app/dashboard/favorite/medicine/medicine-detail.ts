import { Component, Injector } from '@angular/core';
import { Location } from '@angular/common';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';

@Component({
selector: 'phi-medicine-detail',
templateUrl: './medicine-detail.html'
})
export class MedicineDetail extends BaseForm  {

  @select(['conversation']) conversation$;

  Medicine;

  constructor(injector: Injector, private location: Location) {
    super(injector);
    this.conversation$.subscribe(res =>
      this.Medicine = res.Medicine
    );
  }

  backToSearchMedicine() {
    this.location.back();
  }

  close() {
    this.backToSearchMedicine();
  }
}
