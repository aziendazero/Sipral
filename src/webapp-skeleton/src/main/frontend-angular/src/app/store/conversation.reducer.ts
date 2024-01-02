import { ConversationActions } from '../actions/conversation.actions';

/**
 * Created by Alex on 23/06/2017.
 */

const INITIAL_STATE = { };

export function ConversationReducer(state: any = INITIAL_STATE, action: any): any {
  switch (action.type) {
    case ConversationActions.CONVERSATION_PUT:
        return Object.assign({}, state, {
          // loading: false,
          [action.payload.key]: action.payload.value,
          // lastUpdated: action.receivedAt
        });
      // }
    case ConversationActions.CONVERSATION_REMOVE:
      var newState = Object.assign({}, state);
      delete newState[action.payload.key];
      return newState;
    case ConversationActions.CONVERSATION_CLEAN:
      return {};
    default: {
      return state;
    }
  }
}
