/**
 * Created by Alex on 23/06/2017.
 */
import { combineReducers } from 'redux';
import { ConfigReducer, Config } from './config.reducer';
import { ProcessReducer, Processes } from "./process.reducer";
import { BannerReducer, Banner } from "./banner.reducer";
import { ConversationReducer } from "./conversation.reducer";

export class IAppState {
  // Interface stuff
  config: Config;

  // Session
  process: Processes;
  banner: Banner;
  // TODO: Employee

  // Conversation
  conversation: any;

};

export const rootReducer = combineReducers<IAppState>({
  // Interface stuff
  config: ConfigReducer,

  // Session
  process: ProcessReducer,
  banner: BannerReducer,
  // TODO: EmployeeReducer

  // Conversation
  conversation: ConversationReducer,
});
