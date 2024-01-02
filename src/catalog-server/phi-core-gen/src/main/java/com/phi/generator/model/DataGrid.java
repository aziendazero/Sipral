package com.phi.generator.model;

/**
 * Data grid
 * Created by Alex on 21/11/2016.
 */
public class DataGrid extends Layout<DataGridColumn> {

    private Boolean caption;
    private Boolean selectableRow = false;
    private String jollyParameter;

    public DataGrid(String id, String label, String binding, String caption, String selectableRow, String style,
                    String styleClass, String render, String renderedEL, String jollyParameter) {

        this("DataGrid", id, label, binding, caption, selectableRow, style, styleClass, render, renderedEL, jollyParameter);
    }

    protected DataGrid(String type, String id, String label, String binding, String caption, String selectableRow,
                       String style, String styleClass, String render, String renderedEl, String jollyParameter) {

        super(type, id, label, style, styleClass, render, renderedEl, binding);

        if ("true".equals(caption)) {
            this.caption = true;
        }
        if ("true".equals(selectableRow)) {
            this.selectableRow = true;
        }
        this.jollyParameter = jollyParameter;
    }

    public Boolean getCaption() {
        return caption;
    }

    public Boolean getSelectableRow() {
        return selectableRow;
    }

    public String getJollyParameter() {
        return jollyParameter;
    }

//    public String toString() {
//        final StringBuilder result = new StringBuilder();
//        result.append("<");
//        result.append(type);
//
//        appendAttribute(result, "id", id);
//        appendAttribute(result, "label", label);
//
//        appendAttribute(result, "orientation", orientation);
//        appendAttribute(result, "asGroupBox", asGroupBox);
//
//        appendAttribute(result, "caption", caption);
//
//        result.append(">\n");
//
//        appendIndentation(result, 1);
//        result.append("<thead className=\"tableHeader\"><tr>\n");
//        for (DataGridColumn child : sortedChildren) {
//            appendIndentation(result, 2);
//            result.append("<DataGridHeader");
//            appendAttribute(result, "label", child.label);
//            result.append("/>\n");
//        }
//        appendIndentation(result, 1);
//        result.append("</tr></thead>\n");
//
//        appendIndentation(result, 1);
//
//        result.append("<DgBody");
//        appendAttribute(result, "binding", binding);
//        if (selectableRow) {
//            appendAttribute(result, "selectableRow", true);
//        }
//        result.append(" handleSubmit={this.props.handleSubmit}");
//
//        result.append(" createBody={(");
//        result.append(entityName);
//        result.append(", ");
//        result.append(entityName);
//        result.append("Row, rowIndex, ie, injected) => (\n");
//        appendIndentation(result, 2);
//        result.append("<tr key={rowIndex} className={(injected ? 'injected' : '')}");
//        if (selectableRow) {
//            result.append(" onClick={(e) => ie('");
//            result.append(binding);
//            result.append("', rowIndex, e)}");
//        }
//        result.append(">\n");
//        for (DataGridColumn child : sortedChildren) {
//            appendIndentation(result, 3);
//            result.append(child.toString());
//            result.append("\n");
//        }
//        appendIndentation(result, 2);
//        result.append("</tr>\n");
//        appendIndentation(result, 1);
//        result.append(")}/>\n");
//
//        appendIndentation(result);
//        result.append("</");
//        result.append(type);
//        result.append(">");
//
//        return result.toString();
//    }
}