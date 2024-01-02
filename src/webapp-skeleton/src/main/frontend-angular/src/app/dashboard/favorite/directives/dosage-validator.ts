import { Directive, forwardRef } from '@angular/core';
import { NG_VALIDATORS, FormControl } from '@angular/forms';
import { Dosage } from '../../../services/entities/act';


/**
 * Factiory, creates doasge validator
 * @returns {(c: FormControl) => {validateDosage: {valid: boolean}}}
 */
function validateDosageFactory(/*emailBlackList: EmailBlackList*/) {
  return (c: FormControl) => {
    // let EMAIL_REGEXP = /^[a-z0-9!#$%&'*+\/=?^_`{|}~.-]+@[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/i;

    // return EMAIL_REGEXP.test(c.value) ? null : {
    if (c.value === null || !(c.value instanceof Array) || c.value.length === 0) {
      return {
        validateDosage: {
          valid: false
        }
      };
    }

    const seen = new Set();
    const hasDuplicates = c.value.some((d: Dosage) => {
      return seen.size === seen.add(d.daytime.getTime()).size;
    });

    if (hasDuplicates) {
      return {
        validateDosage: {
          valid: false
        }
      };
    }

  };
}

/**
 * Dosage validator
 * Returs false if dosageList is empty or has duplicate values
 */
@Directive({
  selector: '[validateDosage][ngModel],[validateDosage][formControl]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => DosageValidator), multi: true }
  ]
})
export class DosageValidator {

  validator: Function;

  constructor(/*emailBlackList: EmailBlackList*/) {
    this.validator = validateDosageFactory(/*emailBlackList*/);
  }

  validate(c: FormControl) {
    return this.validator(c);
  }
}
