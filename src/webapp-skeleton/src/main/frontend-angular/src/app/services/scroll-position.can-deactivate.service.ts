import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { ConversationActions } from 'app/actions/conversation.actions';


/**
 * Component must have a scrollPosition field
 */
export interface SaveScrollOnDeactivate {
  scrollIdentifier: string;
  scrollPosition: number;
}

/**
 * Save component scrollPosition into redux store conversation.componentNameScrollPosition
 * Usefull to restore position after navigation
 */

@Injectable()
export class ScrollPositionCanDeactivateService implements CanDeactivate<SaveScrollOnDeactivate> {

  constructor(private conversationActions: ConversationActions) { } 

  canDeactivate(component: SaveScrollOnDeactivate) {
    this.conversationActions.put(component.scrollIdentifier + 'ScrollPosition', component.scrollPosition);
    return true;
  }
}
