package com.phi.cs.catalog.adapter.nestedset;

import com.fasterxml.jackson.annotation.JsonBackReference;


//@Embeddable
public class NestedSetNodeInfo<N extends NestedSetNode> {

//    @Parent
    protected N owner;

//    @Column(name = "NS_THREAD", nullable = false, updatable = false)
    protected Long nsThread = 0l;

//    @Column(name = "NS_LEFT", nullable = false, updatable = false)
    protected Long nsLeft = 0l;

//    @Column(name = "NS_RIGHT", nullable = false,  updatable = false)
    protected Long nsRight = 0l;

    protected NestedSetNodeInfo() {}

    public NestedSetNodeInfo(N owner) {
        this.owner = owner;
    }

    public NestedSetNodeInfo(NestedSetNodeInfo<N> original) {
        this.owner = original.owner;
        this.nsLeft = original.nsLeft;
        this.nsRight = original.nsRight;
        this.nsThread = original.nsThread;
    }

    @JsonBackReference
    public N getOwner() {
        return owner;
    }

//    private void setOwner(N owner) {
//        this.owner = owner;
//    }

    public Long getNsThread() {
        return nsThread;
    }

    public void setNsThread(Long nsThread) {
        this.nsThread = nsThread;
    }

    public Long getNsLeft() {
        return nsLeft;
    }

    public void setNsLeft(Long nsLeft) {
        this.nsLeft = nsLeft;
    }

    public Long getNsRight() {
        return nsRight;
    }

    public void setNsRight(Long nsRight) {
        this.nsRight = nsRight;
    }

    public String toString() {
        return "NSInfo LEFT: " + getNsLeft() + " RIGHT: " + getNsRight() + " THREAD: " + getNsThread();
    }
}
