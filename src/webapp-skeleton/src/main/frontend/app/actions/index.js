//export {default as DictionaryManager} from './DictionaryManager.js';
import { hashHistory } from 'react-router';

//export const baseUrl = 'http://localhost:8080/spisal/';
export const baseUrl = 'http://localhost:8080/PHI_CI/';

const dateFormat = /^(\d{4}|[+\-]\d{6})(?:-(\d{2})(?:-(\d{2}))?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(?:\.(\d{3}))?)?(?:(Z)|([+\-])(\d{2})(?::(\d{2}))?)?)?$/;

function reviver(key, value) {
    if (typeof value === "string" && dateFormat.test(value)) {
        return new Date(value);
    }

    return value;
}

const viewStateRegexp = /id=\"javax\.faces\.ViewState\" value="([^\"]+)"/;
const langRegexp = /lang=\"([^\"]+)\"/;

// let header = new Headers({
//     'Access-Control-Allow-Origin':'*',
//     'Content-Type': 'application/json'
// });

/*export const login = (identity) => {
    return {
        type: 'LOGIN',
        identity
    }
}*/

//OLD implementation:
/**
 * public login method
 */
// login(identity) {
//     try {
//
//         jQuery.ajax({
//             type: "POST",
//             //contentType : 'application/json',
//             url: this.baseUrl + 'resource/rest/login',
//             data: identity,
//             //processData: false,
//             success: function(response/*, textStatus, jqXHR*/) {
//                 this.setState({
//                     jsessionId: response.jsessionId
//                 });
//                 //this.jsessionId = response.jsessionId;
//                 this.cid = response.cid;
//                 this.read();
//             }.bind(this),
//             error: function(jqXHR, textStatus, errorThrown) {
//                 console.error('Login failed ' + textStatus + ' ' + errorThrown);
//             }.bind(this)
//         })
//     } catch(e) {
//         console.error("error loginz" + e.stack);
//     }
// }





export const REQUEST_PROCESSES = 'PHI-GetProcesses';
export const RECEIVE_PROCESSES = 'PHI-GetProcesses-response';

/**
 * Get list of all processes
 * @returns {Function}
 */
export function getProcesses() {
    return function (dispatch) {

        dispatch({type: REQUEST_PROCESSES});

        return fetch(baseUrl + 'resource/rest/processmanager/processlist', {credentials: 'include'})
            .then(response => response.json())
            .then(json => dispatch({type: RECEIVE_PROCESSES, processes: json, receivedAt: Date.now()}))
            .catch(error => {
                console.error('Error getting processes list ' + error.message);
                return dispatch({type: RECEIVE_PROCESSES, processes: {}, receivedAt: Date.now()});
            });
    }
}



export const REQUEST_START_PROCESSES = 'PHI-StartProcesses';
export const RECEIVE_START_PROCESSES = 'PHI-StartProcesses-response';

/**
 * Start a process
 * @param process
 * @returns {Function}
 */
export function startProcess(process) {
    return function (dispatch) {

        dispatch({type: REQUEST_START_PROCESSES, process});

        return fetch(baseUrl + 'resource/rest/processmanager', { method: "POST", credentials: 'include', body: 'processName=' + process, headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}})
            .then(response => response.json())
            .then(jsonAndForm => {
                dispatch({type: RECEIVE_START_PROCESSES, process, cid : jsonAndForm.cid, data: jsonAndForm.data, viewId: jsonAndForm.viewId, receivedAt: Date.now()});
                // dispatch({type: RECEIVE_START_PROCESSES, process, cid : jsonAndForm.json.cid, viewId: jsonAndForm.json.viewId, receivedAt: Date.now()});
                return jsonAndForm;
            })
           // .then(json => dispatch(getForm(json)))//FIXME: remove once richfaces is removed
            .catch(error => console.error('Error starting process ' + process + ' ' +  error.message, error));
    }
}

export const REQUEST_MANAGE_TASK = 'PHI-ManageTask';
export const RECEIVE_MANAGE_TASK = 'PHI-ManageTask-response';

/**
 * Manage task of current process
 * @param task
 * @returns {Function}
 */
export function manageTask(task) {
    return function (dispatch, getState) {

        dispatch({type: REQUEST_MANAGE_TASK, task});

        var bodyToSend = 'btnId=' + task.id;

        if (task.mnemonicName != undefined) {
            bodyToSend += '&btnMnemonic=' + task.mnemonicName;
        }

        if (task.inject != undefined) {
            bodyToSend += '&inject=' + task.inject;
        }

        bodyToSend +=  '&fields=' + JSON.stringify(task.registeredFields) + '&values=' + JSON.stringify(task.values);

        return fetch(baseUrl + 'resource/rest/processmanager/managetask?cid=' + getState().process.cid, { method: "POST", credentials: 'include', body: bodyToSend, headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}})
            //.then(response => response.json()) NO REVIVER
            .then(response => response.text())
            .then(text => {
                let json = JSON.parse(text, reviver);
                if (json.viewId == 'home') {
                    hashHistory.push('/');
                }
                dispatch({type: RECEIVE_MANAGE_TASK, task, cid : json.cid, data: json.data, viewId: json.viewId, receivedAt: Date.now()});
            })
            .catch(error => console.error('Error managing task ' + task.id + ' ' +  error.message, error));
    }
}



//FIXME: remove once richfaces is removed
export const REQUEST_GET_FORM = 'PHI-GetForm';
export const RECEIVE_GET_FORM = 'PHI-GetForm-response';

/**
 * Get a jsf form, to be removed
 * @param json
 * @returns {Promise.<TResult>}
 */
export function getForm(cid) {
    return function (dispatch) {
        dispatch({type: REQUEST_GET_FORM});
        let url = baseUrl + 'home-jsf.seam';
        if (cid != null) {
            url += '?cid=' + cid;
        }
        return fetch(url, {credentials: 'include'})
            .then(response => response.text())
            .then(html => {
                let match = viewStateRegexp.exec(html);
                let viewState = match[1];
                match = langRegexp.exec(html);
                let lang = match[1];
                return dispatch({type: RECEIVE_GET_FORM, viewState, 'lang':lang, receivedAt: Date.now()})
            })
            .catch(error => console.error('Error getting form ' + url + error.message))
    }
}



export const REQUEST_END_PROCESSES = 'PHI-EndProcess';
export const RECEIVE_END_PROCESSES = 'PHI-EndProcess-response';

/**
 * End a process
 * @param process
 * @returns {Function}
 */
export function endProcess(process) {
    return function (dispatch) {

        dispatch({type: REQUEST_END_PROCESSES});
        //FIXME add call to server
        // return fetch(baseUrl + 'resource/rest/processmanager', { method: "POST", credentials: 'include', body: 'processName=' + process, headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}})
        //     .then(response => response.json())
        //     .then(json =>
                dispatch({type: RECEIVE_END_PROCESSES, process, receivedAt: Date.now()});
        //     )
        //     .catch(error => console.error('Error starting process ' + process + error.message));

    }
}




export const REQUEST_INJECT = 'PHI-Inject';
export const RECEIVE_INJECT = 'PHI-Inject-response';

/**
 * Inject an entity
 * @param entity
 * @returns {Function}
 */
export function inject(listName, listIndex) {
    return function (dispatch, getState) {

        dispatch({type: REQUEST_INJECT, listName, listIndex});

        var entityName = listName.substring(0, listName.length-4);

        var bodyToSend = 'listName=' + listName;
        bodyToSend += '&listIndex=' + listIndex;

        return fetch(baseUrl + 'resource/rest/processmanager/inject?cid=' + getState().process.cid, { method: "POST", credentials: 'include', body: bodyToSend, headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}})
        //.then(response => response.json()) NO REVIVER
            .then(response => response.text())
            .then(text => {
                let entity = JSON.parse(text, reviver);
                dispatch({type: RECEIVE_INJECT, entityName, data: entity, receivedAt: Date.now()});
            })
            .catch(error => console.error('Error injecting ' + listName + error.message));
    }
}

export const REQUEST_DASHBOARD_REFRESH = 'PHI-Dashboard-refresh';
export const RECEIVE_DASHBOARD_REFRESH = 'PHI-Dashboard-refresh-response';

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

export function dashboardRefresh(dashboardName) {
    return function (dispatch) {
        dispatch({type: REQUEST_DASHBOARD_REFRESH});
        //var url = baseUrl + "resource/rest/" + dashboardName + "/refresh/" +
        // "showheld=false;encounterTypes=IMP;encounterTypes=DH;encounterTypes=DSRG;encounterTypes=DSRV" +
        // "encounterTypes=SAMB;assigned=true;sorting=SurnameNameASC;statusCodes=new;statusCodes=active;" +
        // "statusCodes=held;temporary=true;wards=11;wards=45313;wards=18;wards=71371;wards=24;wards=37;/1"; //?cid=" + json.cid;

        var url = baseUrl + "resource/rest/" + dashboardName + "/init/" +
            "statusCodes=new;statusCodes=active;statusCodes=held;wards=71359;wards=37;encounterTypes=IMP;" +
            "encounterTypes=DH;encounterTypes=DSRG;encounterTypes=DSRV;encounterTypes=SAMB;temporary=true;" +
            "sorting=SurnameNameASC;assigned=true;/1";//?cid=477&_=1492504712711";

        return fetch(url, {credentials: 'include'})
            .then(response => response.text())
            .then(text => {
                let json = JSON.parse(text, reviver);
                return dispatch({type: RECEIVE_DASHBOARD_REFRESH, data: json.main.entities, entityName: capitalizeFirstLetter(dashboardName), receivedAt: Date.now()})
            })
            //return {json, formData: text}})
            .catch(error => console.error('Error refreshing dashboard ' + url + error.message))
    }
}