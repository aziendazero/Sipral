import {baseUrl} from './index.js'

export const REQUEST_GET = 'PHI-DictionaryManager-get';
export const RECEIVE_GET = 'PHI-DictionaryManager-get-response';

export default class DictionaryManager {

    constructor(){
        this.restBaseUrl = "resource/rest/";
        this.entityUrl = "codevalues";

        this.GET_ENTITY = "/";
        this.GET_DOMAIN = "/domain/";
        this.SEARCH_CODE_VALUES = "/search/";
        this.SUGGEST = "/suggest/";

        // this.REQUEST_GET = 'PHI-DictionaryManager-get';
        // this.RECEIVE_GET = 'PHI-DictionaryManager-get-response';

    }

    /**
     * Get codeValues by id codeSystem and domain d=CodeSystem:Domain:code1,code2,code3
     * @param domain
     * @returns {Function}
     */
    get(codeSystemAndDomains, forceReload = false, lazy = false) {
        return function (dispatch) {

            let url = baseUrl + this.restBaseUrl + this.entityUrl + this.GET_DOMAIN;
            if (lazy)
                url += "lazy=true";
            else
                url += "lazy=false";

            url += ";domain=" + encodeURIComponent(codeSystemAndDomains);

            dispatch({type: REQUEST_GET, codeSystemAndDomains});

            return fetch(url, { method: "GET", credentials: 'include'})
                .then(response => response.json())
                .then(codeValues => dispatch({type: RECEIVE_GET, codeValues, receivedAt: Date.now()}) )
                .catch(error => console.error('Error getting domain ' + codeSystemAndDomains + ' ' +  error.message, error));
        }.bind(this);
    }

}