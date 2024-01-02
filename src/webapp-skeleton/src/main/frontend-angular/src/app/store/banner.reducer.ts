/**
 * Created by Daniele on 26/06/2017.
 */
import {BannerActions} from '../actions/banner.action';

export interface Banner {
  visible: boolean;
}
const INITIAL_STATE: Banner = { visible: false};

export function BannerReducer(state:Banner = INITIAL_STATE, action: any): any {
  switch (action.type) {
    case BannerActions.BANNER_PUT:
      if (!action.payload) {
        action.payload = {visible: false};
      } else {
        action.payload.visible = true;
      }
      return action.payload;
    case BannerActions.BANNER_REMOVE:
      return {visible: false};
    default: {
      return state;
    }
  }
}
