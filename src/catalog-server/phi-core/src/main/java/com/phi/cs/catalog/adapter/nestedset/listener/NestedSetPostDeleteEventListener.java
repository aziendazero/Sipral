/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.phi.cs.catalog.adapter.nestedset.listener;

import org.apache.log4j.Logger;
import org.hibernate.ejb.event.EJB3PostDeleteEventListener;
import org.hibernate.event.PostDeleteEvent;

import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;

/**
 * Executes the nested set tree traversal after a node was deleted.
 *
 * @author Christian Bauer
 */
public class NestedSetPostDeleteEventListener extends EJB3PostDeleteEventListener {

	private static final Logger log = Logger.getLogger(NestedSetPostDeleteEventListener.class);

    public void onPostDelete(PostDeleteEvent event) {
        super.onPostDelete(event);

        if ( NestedSetNode.class.isAssignableFrom(event.getEntity().getClass())) {
            log.debug("executing nested set delete operation, recalculating the tree");
            new DeleteNestedSetOperation( (NestedSetNode)event.getEntity() ).execute(event.getSession());
        }
    }

}
