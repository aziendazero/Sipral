package com.phi.generator.model;

/**
 * Tabbed panel
 * Created by Alex on 25/11/2016.
 */
public class TabbedPanel extends DataGrid {

    public TabbedPanel(String id, String label, String binding, String caption, String selectableRow, String style,
                       String styleClass, String render, String renderedEL, String jollyParameter) {
        super("TabbedPanel", id, label, binding, caption, selectableRow, style, styleClass, render, renderedEL, jollyParameter);
    }

//    public String toString() {
//        final StringBuilder result = new StringBuilder();
//        result.append("<");
//        result.append(type);
//
//        appendAttribute(result, "id", id);
//        result.append(">\n");
//
//        for (Object child : sortedChildren) {
//            appendIndentation(result);
//            result.append("<TabbedPanel.Panel title=\"");
//            result.append(((Layout)child).label);
//            result.append("\">\n");
//            appendIndentation(result, 1);
//            result.append(child.toString());
//            result.append("\n");
//            appendIndentation(result);
//            result.append("</TabbedPanel.Panel>\n");
//        }
//
//        appendIndentation(result);
//        result.append("</");
//        result.append(type);
//        result.append(">");
//
//        return result.toString();
//    }
}
