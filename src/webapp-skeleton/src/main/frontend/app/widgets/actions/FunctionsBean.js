/**
 * com.phi.cs.view.bean.FunctionsBean javascript implementation
 */

export function isEmpty(object) {
    if (object == null) {
        return true;
    } else {
        return false;
    }
}

export default class FunctionsBean {

    hasCodeIn(code, codeList) {
        //return codeList.indexOf(code) != -1;
        return codeList == code;
    }

    /**
     * Procpratiche.vigilanza.reason, "id"
     */
    propertyAsList(list, propertyInList) {
        console.log('functions.propertyAsListTo be implemented!');
    }

}