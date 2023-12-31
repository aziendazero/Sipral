/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.phi.cs.catalog.adapter.nestedset.query;

import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;

import java.util.*;

/**
 * Wraps a {@link com.phi.cs.catalog.adapter.nestedset.NestedSetDelegate} and links it into a read-only tree of parent and children.
 * <p>
 * This wrapper is returned by the {@link NestedSetResultTransformer}. For example,
 * you query your tree with a nested set query starting from a particular node. You
 * want all children of that start node, including their children, and so on. The
 * {@link NestedSetResultTransformer} will handle your query result, which represents
 * a flat subtree, and link together the nodes in a hierarchical fashion. You will get
 * back your start node in a {@link NestedSetNodeWrapper} and you can access the
 * children and their children, and so on, through the <tt>wrappedChildren</tt> collection
 * of the wrapper. The regular <tt>children</tt> collection of the wrapped
 * {@link com.phi.cs.catalog.adapter.nestedset.NestedSetDelegate} owner instances are not initialized! Use
 * the wrapper tree to display the data or to work with the whole subtree. As a bonus you also get
 * the <tt>level</tt> of each node in the (sub)tree you queried. You can access (but not
 * modify) the linked parent of each wrapped node through <tt>wrappedParent</tt>.
 * </p>
 * <p>
 * The <tt>wrappedChildren</tt> of each wrapper are by default in a {@link java.util.List}.
 * You can also access the same nodes through the <tt>getWrappedChildrenSorted()</tt> method,
 * which returns a {@link java.util.SortedSet} that is sorted with the {@link java.util.Comparator}
 * supplied at construction time. This means that in-level sorting (how the children of a particular node
 * are sorted) does not occur in the database but in memory. This should not be a performance problem,
 * as you'd usually query for quite small subtrees, most of the time to display a
 * subtree. The comparator usually sorts the collection by some property of the
 * wrapped {@link com.phi.cs.catalog.adapter.nestedset.NestedSetDelegate} owner.
 * </p>
 * <p>
 * Note: Do not modify the collections or the parent reference of the wrapper, these
 * are read-only results and modifications are not reflected in the database.
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetNodeWrapper<N extends NestedSetNode> {

    N wrappedNode;
    NestedSetNodeWrapper<N> wrappedParent;
    List<NestedSetNodeWrapper<N>> wrappedChildren = new ArrayList<NestedSetNodeWrapper<N>>();
    Comparator<NestedSetNodeWrapper<N>> comparator;
    Long level;
    Map<String, Object> additionalProjections = new HashMap<String, Object>();
    public boolean childrenLoaded = false;

    public NestedSetNodeWrapper(N wrappedNode) {
        this(
            wrappedNode,
            // Default comparator uses identifiers of wrapped nodes
            new Comparator<NestedSetNodeWrapper<N>>() {
                public int compare(NestedSetNodeWrapper<N> o1, NestedSetNodeWrapper<N> o2) {
                	
                    //return o1.getWrappedNode().getInternalId().compareTo(o2.getWrappedNode().getInternalId());
//                	return Long.compare(o1.getWrappedNode().getInternalId(), o2.getWrappedNode().getInternalId());
                	return compareLong(o1.getWrappedNode().getInternalId(), o2.getWrappedNode().getInternalId());
                	
                }
            }
        );
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator) {
        this(wrappedNode, comparator, 0l);
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level) {
        this(wrappedNode, comparator, level, new HashMap<String,Object>());
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level, Map<String,Object> additionalProjections) {
        if (wrappedNode == null) {
            throw new IllegalArgumentException("Can't wrap null node");
        }
        this.wrappedNode = wrappedNode;
        this.comparator = comparator;
        this.level = level;
        this.additionalProjections = additionalProjections;
    }

    public N getWrappedNode() {
        return wrappedNode;
    }

    void setWrappedNode(N wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    public NestedSetNodeWrapper<N> getWrappedParent() {
        return wrappedParent;
    }

    void setWrappedParent(NestedSetNodeWrapper<N> wrappedParent) {
        this.wrappedParent = wrappedParent;
        childrenLoaded = true;
    }

    public List<NestedSetNodeWrapper<N>> getWrappedChildren() {
        return wrappedChildren;
    }

    void setWrappedChildren(List<NestedSetNodeWrapper<N>> wrappedChildren) {
        this.wrappedChildren = wrappedChildren;
    }

    void addWrappedChild(NestedSetNodeWrapper<N> wrappedChild) {
        getWrappedChildren().add(wrappedChild);
        childrenLoaded = true;
    }

    public Comparator<NestedSetNodeWrapper<N>> getComparator() {
        return comparator;
    }

    public Long getLevel() {
        return level;
    }

    public Map<String, Object> getAdditionalProjections() {
        return additionalProjections;
    }

    public SortedSet<NestedSetNodeWrapper<N>> getWrappedChildrenSorted() {
        SortedSet<NestedSetNodeWrapper<N>> wrappedChildrenSorted = new TreeSet<NestedSetNodeWrapper<N>>(comparator);
        wrappedChildrenSorted.addAll(getWrappedChildren());
        return wrappedChildrenSorted;
    }

    // This is needed because JSF converters for selectitems need to return an equal() instance to
    // the selected item of the selectitems collection. This sucks.
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NestedSetNodeWrapper that = (NestedSetNodeWrapper) o;

        //if (!wrappedNode.getInternalId().equals(that.wrappedNode.getInternalId())) return false;
        if (wrappedNode.getInternalId() != that.wrappedNode.getInternalId()) return false;

        return true;
    }

    public int hashCode() {
        //return wrappedNode.getInternalId().hashCode();
    	return (int) (wrappedNode.getInternalId() ^ (wrappedNode.getInternalId() >>> 32));
    }

    public String toString() {
        return "Wrapper on level " + getLevel() + " for: " + getWrappedNode();
    }
    
    /**
     * Copied from openJdk 7 method Long.compare
     * @param x
     * @param y
     * @return
     */
    private static int compareLong(long x, long y) {
    	return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}

