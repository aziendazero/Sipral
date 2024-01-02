/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.phi.cs.catalog.adapter.nestedset.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.event.EventSource;
import org.hibernate.util.LazyIterator;

import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;

/**
 * The contract of a nested set operation sequence as executed in a Hibernate event listener.
 * <p>
 * Guarantees that first the database tree nodes are updated, then the in-memory nodes
 * currently managed by the persistence context.
 * </p>
 * To access the database, an operation uses a <tt>StatelessSession</tt> of Hibernate, and
 * it obtains a JDBC connection using the Hibernate connection provider. If run in an
 * application server with JTS/JTA, the <tt>getConnection()</tt> method
 * returns the same connection handle that is already used inside the current transaction.
 * This means we run on the same connection and transaction as the rest of the Hibernate flush event
 * that executes the <tt>NestedSetOperation</tt>. However, if you run this outside of a managed
 * environment, a new JDBC connection might be obtained from the JDBC connection pool.
 * In that case, you should enable auto-commit mode in your Hibernate configuration. Or,
 * if you want the database tree updates to be atomic and isolated (a good idea), you can
 * override the <tt>beforeExecution()</tt> and <tt>afterExecution()</tt> methods and begin
 * and commit a database transaction manually. Note that this still would be outside the
 * initial connection and transaction, and therefore not be atomic with the overall tree
 * manipulation. This can be improved as soon as Hibernate implements a new contract
 * for the deprecated <tt>Session#connection()</tt> method.
 * </p>
 *
 * TODO: We should lock the tables!
 *
 * @author Christian Bauer
 */
public class NestedSetOperation {

    private static final Logger log = Logger.getLogger(NestedSetOperation.class);

    protected NestedSetNode node;
    protected String nodeEntityName;

    public NestedSetOperation(NestedSetNode node) {
        this.node = node;
        this.nodeEntityName = node.getClass().getName();
    }

    // The main sequence of the operation, override to implement your operation

    protected void beforeExecution() {}
    protected void executeOnDatabase(EventSource session) {}
    protected void executeInMemory(Collection<NestedSetNode> inMemoryState) {}
    protected void afterExecution() {}

    // The procedure that executes the sequence of the operation

    public void execute(EventSource session) {
//        StatelessSession ss = null;
//        Connection jdbcConnection = null;
        try {
//            jdbcConnection = getConnection(session);
//            log.trace("opening new session on existing connection");
//            ss = session.getSessionFactory().openStatelessSession(jdbcConnection);

            beforeExecution();
            executeOnDatabase(session);

            // Find all NestedSetNode instances in the persistence context
            Collection<NestedSetNode> nodesInPersistenceContext = new HashSet<NestedSetNode>();
            Iterator contextIterator = new LazyIterator( session.getPersistenceContext().getEntitiesByKey() );
            while (contextIterator.hasNext()) {
                Object o = contextIterator.next();
                if (NestedSetNode.class.isAssignableFrom(o.getClass())) nodesInPersistenceContext.add((NestedSetNode)o);
            }

            executeInMemory(nodesInPersistenceContext);
            afterExecution();

        } catch (Exception ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        } 
//        finally {
//            if (ss != null) {
//                try {
//                    log.trace("closing session and connection");
//                    jdbcConnection.close();
//                    ss.close();
//                } catch(SQLException ex) {
//                    log.error("could not close connection or session", ex);
//                }
//            }
//        }
    }

//    protected Connection getConnection(Session session) throws Exception {
//        // We do not use session.connection() because it conflicts with Hibernates aggressive collection release
//        return ((SessionFactoryImpl)session.getSessionFactory()).getConnectionProvider().getConnection();
//    }

}
