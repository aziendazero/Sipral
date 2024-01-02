import * as Actions from '../actions'

const initialState = {
    loading: false,
    processes: []
};

const process = (state = initialState, action) => {
    switch (action.type) {
        case Actions.REQUEST_PROCESSES:
            return Object.assign({}, state, {
                loading: false
            });
        case Actions.RECEIVE_PROCESSES:
            return Object.assign({}, state, {
                loading: false,
                processes: action.processes.processList,
                param: action.processes.param,
                lastUpdated: action.receivedAt
            });

        case Actions.REQUEST_START_PROCESSES:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_START_PROCESSES:
            return Object.assign({}, state, {
                loading: false,
                viewId: action.viewId,
                cid: action.cid,
                data: action.data,
                lastUpdated: action.receivedAt
            });

        case Actions.REQUEST_MANAGE_TASK:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_MANAGE_TASK:
            let viewId = action.viewId;
            let data = action.data;
            if (viewId == 'home') {
                viewId = null;
                data = null;
            }
            return Object.assign({}, state, {
                loading: false,
                viewId: viewId,
                data: data
            });

        case Actions.REQUEST_END_PROCESSES:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_END_PROCESSES:
            return Object.assign({}, state, {
                loading: false,
                viewId: null,
                data: null,
                lastUpdated: action.receivedAt
            });

        case Actions.REQUEST_INJECT:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_INJECT:
            var newData = Object.assign({}, state.data);
            newData[action.entityName] = action.data;
            return Object.assign({}, state, {
                loading: false,
                data: newData
            });

        case Actions.REQUEST_DASHBOARD_REFRESH:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_DASHBOARD_REFRESH:
            var dashData = Object.assign({}, state.data);
            dashData[action.entityName] = action.data;
            return Object.assign({}, state, {
                loading: false,
                data: dashData
            });

        default:
            return state
    }
};

export default process