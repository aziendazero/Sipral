import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
/**
 * Class with static methods to get the statusIcon and the statusColor of an erogation (SubstanceAdministration or LEPExecution)
 * Same as: /MDashboard/src/com/phi/dataModels/ErogationStatus.as
 */
@Injectable()
export class ErogationStatus {
  // Disabled future (now < planned - 60m)
  public static DUE_FUTURE_DISABLED:String 	= "DUE_FUTURE_DISABLED";
  // Enabled early (planned - 60m < now < planned - 30m)
  public static DUE_FUTURE_ENABLED:String 	= "DUE_FUTURE_ENABLED";
  // Enabled right (planned - 30m < now < planned + 30m)
  public static DUE:String 					= "DUE";
  // Enabled delay (planned + 30m < now < planned + 60m)
  public static OVERDUE:String 				= "OVERDUE";
  // Disabled passed (now > planned + 60m) (super)
  public static MISSED:String 				= "MISSED";

  // Erogated in range (planned - 60m / planned - 30m)
  public static EROGATED_EARLY:String 		= "EROGATED_EARLY";
  // Erogated in range (planned - 30m / planned + 30m)
  public static EROGATED_AS_PLANNED:String 	= "EROGATED_AS_PLANNED";
  // Erogated in range (planned + 30m / planned + 60m)
  public static EROGATED_LATE:String 			= "EROGATED_LATE";

  // Erogated with exception (super)
  public static EXCEPTION:String 						= "EXCEPTION";
  public static EXCEPTION_WRONG_DRUG:String 		= "EXCEPTION_WRONG_DRUG";
  public static EXCEPTION_WRONG_DOSE:String 		= "EXCEPTION_WRONG_DOSE";
  public static EXCEPTION_OTHER_REASON:String		= "EXCEPTION_OTHER_REASON";

  // Not Erogated (super)
  public static UNSUCCESSFUL:String 					= "UNSUCCESSFUL";
  public static UNSUCCESSFUL_PATIENT_REFUSED:String 	= "UNSUCCESSFUL_PATIENT_REFUSED";
  public static UNSUCCESSFUL_PATIENT_ABSENT:String 		= "UNSUCCESSFUL_PATIENT_ABSENT";
  public static UNSUCCESSFUL_DRUG_UNAVAILABLE:String 	= "UNSUCCESSFUL_DRUG_UNAVAILABLE";
  public static UNSUCCESSFUL_NIL_BY_MOUTH:String 		= "UNSUCCESSFUL_NIL_BY_MOUTH";
  public static UNSUCCESSFUL_OTHER_REASON:String		= "UNSUCCESSFUL_OTHER_REASON";

  // Planned (super)
  public static PLANNED:String = "PLANNED";

  // Cancelled (super)
  public static CANCELLED:String = "CANCELLED";

  // Done (super)
  public static DONE:String = "DONE";

  constructor(private translateService: TranslateService) { }

  public getErogationString(status): string {

    let result = '';

    switch (status) {
      case ErogationStatus.DUE_FUTURE_DISABLED:			break;
      case ErogationStatus.DUE_FUTURE_ENABLED:			break;
      case ErogationStatus.DUE: 										break;
      case ErogationStatus.OVERDUE: 								break;
      case ErogationStatus.CANCELLED:		 						break;
      case ErogationStatus.MISSED:									break;

      case ErogationStatus.EROGATED_EARLY:				        result = this.translateService.instant("Erogated_early");			  break;
      case ErogationStatus.EROGATED_AS_PLANNED: 			    result = this.translateService.instant("Erogated_as_planned");		break;
      case ErogationStatus.EROGATED_LATE:					        result = this.translateService.instant("Erogated_late");;			  break;

      case ErogationStatus.EXCEPTION:						          result = this.translateService.instant("Exception");															                        break;
      case ErogationStatus.EXCEPTION_WRONG_DRUG:			    result = this.translateService.instant("Exception") + " - " + this.translateService.instant("Wrong_drug");				    break;
      case ErogationStatus.EXCEPTION_WRONG_DOSE:			    result = this.translateService.instant("Exception") + " - " + this.translateService.instant("Wrong_dose");				    break;
      case ErogationStatus.EXCEPTION_OTHER_REASON:		    result = this.translateService.instant("Exception") + " - " + this.translateService.instant("Other_clinical_reason");	break;

      case ErogationStatus.UNSUCCESSFUL:					        result = this.translateService.instant("Unsuccessful");														                          break;
      case ErogationStatus.UNSUCCESSFUL_PATIENT_REFUSED:	result = this.translateService.instant("Unsuccessful") + " - " + this.translateService.instant("Patient_refused");		    break;
      case ErogationStatus.UNSUCCESSFUL_PATIENT_ABSENT:	  result = this.translateService.instant("Unsuccessful") + " - " + this.translateService.instant("Patient_absent");			    break;
      case ErogationStatus.UNSUCCESSFUL_DRUG_UNAVAILABLE:	result = this.translateService.instant("Unsuccessful") + " - " + this.translateService.instant("Drug_unavailable");		    break;
      case ErogationStatus.UNSUCCESSFUL_NIL_BY_MOUTH:		  result = this.translateService.instant("Unsuccessful") + " - " + this.translateService.instant("Nil_by_mouth");	          break;
      case ErogationStatus.UNSUCCESSFUL_OTHER_REASON: 	  result = this.translateService.instant("Unsuccessful") + " - " + this.translateService.instant("Other_clinical_reason");	break;
    }

    return result;
  }

  public getStatusClass(activity) {
    if (activity.status === 'DUE' && !activity.planneddate ) {
      return 'AS_NEEDED';
    } else if (activity.continuous === 1 && activity.erogationdate !== null && activity.erogationstopdate === null) {
      return 'EROGATING';
    } else {
      return activity.status;
    }
  }
}
