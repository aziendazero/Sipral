import { ProcessActions } from '../actions/process.actions';
import { Process } from '../views/home/components/menu/menu.component';

/**
 * Created by Alex on 23/06/2017.
 */
export interface Processes {
  definitions?: Array<Process>;
  current?: string;
  cid?: number;
  lang?: string;
}
const INITIAL_STATE: Processes = { definitions: [] } as Processes;

export function ProcessReducer(state: Processes = INITIAL_STATE, action: any): Processes {
  let newState;
  switch (action.type) {
    case ProcessActions.PROCESS_GET:
      sessionStorage['cid'] = action.payload.cid;
      return Object.assign({}, state, {
        // loading: false,
        definitions: action.payload.processList,
        cid: parseInt(action.payload.cid),
        version: action.payload.version
        // lastUpdated: action.receivedAt
      });
    case ProcessActions.PROCESS_GET_FORM:
      return Object.assign({}, state, {
        // loading: false,
        viewState: action.payload.viewState,
        lang: action.payload.lang,
        // lastUpdated: action.receivedAt
      });
    case ProcessActions.PROCESS_END:
      sessionStorage['cid'] = action.payload.cid;
      newState = Object.assign({}, state, {
        // loading: false,
        cid: parseInt(action.payload.cid),
        // lastUpdated: action.receivedAt
      });
      delete newState['current'];
      return newState;

    case ProcessActions.PROCESS_NEW_CID:
      sessionStorage['cid'] = action.payload.cid;
      newState = Object.assign({}, state, {
        // loading: false,
        cid: parseInt(action.payload.cid),
        // lastUpdated: action.receivedAt
      });
      delete newState['current'];
      return newState;

    case ProcessActions.PROCESS_CURRENT_PROCESS:
      return Object.assign({}, state, {
        current: action.payload.process,
      });

    case ProcessActions.PROCESS_DASHBOARD: // dashboard url to activate when process end
      return Object.assign({}, state, {
        dashboard: action.payload.dashboard,
      });

    case ProcessActions.PROCESS_SET_LANG: // dashboard url to activate when process end
      return Object.assign({}, state, {
        lang: action.payload.lang,
      });

    case ProcessActions.PROCESS_CLEAN:
      delete sessionStorage['cid'];
      return {};

    default: {
      return state;
    }
  }
}
