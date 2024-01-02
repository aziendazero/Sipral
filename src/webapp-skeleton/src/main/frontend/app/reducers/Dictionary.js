//import * as Actions from '../actions'
import {REQUEST_GET, RECEIVE_GET} from '../actions/DictionaryManager'


const initialState = {
};

const dictionary = (state = initialState, action) => {
    switch (action.type) {
        case REQUEST_GET:
            return Object.assign({}, state, {
                loading: true
            });
        case RECEIVE_GET:
            let newState = Object.assign({}, state, action.codeValues);
            newState.loading = false;
            return newState;
        default:
            return state
    }
};

export default dictionary