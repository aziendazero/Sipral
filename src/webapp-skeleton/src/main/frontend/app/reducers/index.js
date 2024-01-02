import { combineReducers } from 'redux'
import { reducer as formReducer } from 'redux-form'
import process from './Process'
import form from './Form'
import dictionary from './Dictionary'

const phiApp = combineReducers({
    form: formReducer,
    process,
    dictionary,
    formJsf: form
});

export default phiApp