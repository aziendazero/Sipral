import { ConfigActions } from '../actions/config.action';
import { Employee } from '../services/entities/role/employee';
import { SelectItem } from '../services/datamodel/select-item';
/**
 * Created by Alex on 23/06/2017.
 */
export interface Config {
  menuVisible: boolean;
  enabledSdlocs: Array<any>;
  sdLocs: Array<any>;
  employee: Employee;
  employeeRoleCode: string;
  employeeRole: string;
  enabledRoles: Array<SelectItem>
  sid: string;
  param: Map<string, any>;
  errors: Array<any>;
  flashVars: string;
  passwordExpired: boolean;
  options: boolean;
  error: any;
  alertMessage: any;
  isLoggedByServlet: boolean;
  isLockedNode: boolean;
}

const INITIAL_STATE: Config = {
  menuVisible: false,
  enabledSdlocs: null,
  sdLocs: null,
  employee: null,
  employeeRoleCode: null,
  employeeRole: null,
  enabledRoles: null,
  sid: null,
  param: new Map<string, any>(),
  errors: null,
  flashVars: null,
  passwordExpired: false,
  options: false,
  error: null,
  alertMessage: null,
  isLoggedByServlet: false,
  isLockedNode: false
};

export function ConfigReducer(state: Config = INITIAL_STATE, action: any): any {
  let newState;
  switch (action.type) {
    case ConfigActions.CONFIG_PUT:
      newState = Object.assign( // clone actual state and ad the new key: value
        {},
        state,
        {[action.payload.key]: action.payload.value}
      );
      return newState;

    case ConfigActions.CONFIG_REMOVE:
      newState = Object.assign({}, state); // clone actual state
      delete newState[action.payload.key]; // remove the value from the key
      return newState;

    case ConfigActions.CONFIG_TOGGLE_MENU:
      newState = Object.assign({}, state); // clone actual state
      newState.menuVisible = !newState.menuVisible; // toggle visibility in the new state
      return newState;

    case ConfigActions.CONFIG_CLEAN:
      return INITIAL_STATE;

    default: {
      return state;
    }
  }
}
