package com.phi.generator.model;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;

import java.util.LinkedList;

/**
 * Data grid column
 * Created by Alex on 21/11/2016.
 */
public class DataGridColumn extends Layout<Widget> {

    private String sortable;

    public DataGridColumn() {
        super("DataGridColumn");
    }

    public DataGridColumn(String id, String label, String binding, String style, String styleClass, String renderedEl, String sortable) {
        super("DataGridColumn", id, label, style, styleClass, renderedEl);
        this.binding = binding;
        this.sortable = sortable;
    }


    public String getSortable() {
        String sortFunction = null;
        if (sortable == null || sortable.equals("yes")) {
            if (!sortedChildren.isEmpty() && getSortedChildren().get(0).binding != null) { //FIXME fake label if column has no children but a binding no funzia
                String childBinding = getSortedChildren().get(0).binding;
                if (parent.binding != null && childBinding != null) {
                    Expression e = parseEl(childBinding);
                    if (e instanceof FieldAccessExpr) {
                        childBinding = childBinding.substring(childBinding.indexOf('.') + 1, childBinding.length());
                        sortFunction = parent.binding + ".orderBy('" + childBinding + "')";
                    }
                }
            }
        } else if (sortable == "force client sorting") {
            sortFunction = "sorta(this)"; // FIXME
        }
        return sortFunction;
    }

    public String getSortArrow() {
        String sortArrow = null;
        if (sortable == null || !sortable.equals("no")) {
            if (!sortedChildren.isEmpty() && getSortedChildren().get(0).binding != null) {
                String childBinding = getSortedChildren().get(0).binding;
                if (childBinding != null) {
                    childBinding = childBinding.substring(childBinding.indexOf('.') + 1, childBinding.length());
                    sortArrow = "<i *ngIf=\"" + parent.binding + "?.sortColumn('" + childBinding + "')\" [class]=\"" + parent.binding + ".sortArrow\"></i>";
                }
            }
        }
        return sortArrow;
    }

    /**
     * Override to retun fake label if column has no children but a binding
     * @return fake label if column has no children but a binding
     */
    public LinkedList<Widget> getSortedChildren() {
        if (sortedChildren.isEmpty() && binding != null) {
            Widget label = new Widget(this, "Label", binding);
            LinkedList<Widget> fakeChildren = new LinkedList<>();
            fakeChildren.add(label);
            return fakeChildren;
        }
        return sortedChildren;
    }

}
