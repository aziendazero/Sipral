import * as Actions from '../actions'

const initialState = {
};

const form = (state = initialState, action) => {
    switch (action.type) {
        case Actions.REQUEST_GET_FORM:
            return Object.assign({}, state, {
                loading: true
            });
        case Actions.RECEIVE_GET_FORM:
            return Object.assign({}, state, {
                loading: false,
                //formData: action.formData,
                viewState : action.viewState,
                lang: action.lang,
                lastUpdated: action.receivedAt
            });
        default:
            return state
    }
};

export default form