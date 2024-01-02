package com.phi.generator.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Generic layout
 * Created by Alex on 18/11/2016.
 */
public class Layout<T extends BaseComponent> extends BaseComponent {

    private final String LIST_SUFFIX = "List";
    private final String HISTORY_SUFFIX = "History";

    protected String label;

    protected LinkedList<T> sortedChildren = new LinkedList<T>();
    protected Map<String,T> children = new HashMap<String,T>();

    //Layout properties
    protected String orientation;
    protected String asGroupBox;

    //Form properties
    private String fileName; //FIXME remove
    protected String formName; //FIXME remove
    protected String formNameCamelCase;

    protected String style;
    protected String styleClass;

    protected String alignment;

    protected String render;
    protected String renderedEl;

    protected String tabRenderCondition;

    protected String entityName = "row";

    // Non phi model attribute
    private String clazz;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LinkedList<T> getSortedChildren() {
        return sortedChildren;
    }

    public void setSortedChildren(LinkedList<T> sortedChildren) {
        this.sortedChildren = sortedChildren;
    }

    public Map<String, T> getChildren() {
        return children;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getAsGroupBox() {
        return asGroupBox;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (fileName != null) {
            this.formName = fileName.substring(0, fileName.lastIndexOf("."));
            this.formNameCamelCase = BaseComponent.kebabToCamel(this.formName);
        }
    }

    public String getFormName() {
        return formName;
    }

    public String getFormNameCamelCase() {
        return formNameCamelCase;
    }

    public String getStyle() {
        return style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getAlignment() {
        return alignment;
    }

    public String getRender() {
        return render;
    }

    public String getRenderedEl() {
        if (parent instanceof TabbedPanel && (parent.getBinding() == null || parent.getBinding().isEmpty())) {
            return parent.getId() + ".selectedTabIndex == " + parent.getSortedChildren().indexOf(this);
        } else {
            return renderedEl;
        }
    }

    public String getTabRenderCondition() {
        return renderedEl;
    }

    public String getClazz() {
        return clazz;
    }

    public String getEntityName() {
        return entityName;
    }

    public Layout() {
        super();
    }

    public Layout(String type) {
        this.type = type;
    }

    public Layout(String type, String id, String label, String renderedEl) {
        this(type);
        this.id = id;
        this.label = label;
        if (renderedEl != null && !renderedEl.isEmpty()) {
            this.renderedEl = el2javascript(renderedEl);
        }
    }

    public Layout(String type, String id, String label, String style, String styleClass, String renderedEl) {
    	this(type, id, label, renderedEl);
        if (this.style != null) {
            this.style ="{" + style2javascript(style) + "}";
        }
        this.styleClass = styleClass;
    }

    public Layout(String type, String id, String label, String style, String styleClass, String render, String renderedEl, String binding) {
        this(type, id, label, style, styleClass, renderedEl);
        this.render = render;
        this.binding = binding;

        if ("".equals(this.binding)) {
            this.binding = null;
        }

        if (this.binding != null) {
            if (this.binding.endsWith(LIST_SUFFIX)) {
                entityName = this.binding.substring(0,this.binding.length() - LIST_SUFFIX.length());
                if (entityName.endsWith(HISTORY_SUFFIX)) {
                    entityName = entityName.substring(0,entityName.length() - HISTORY_SUFFIX.length());
                }
            } else {
                entityName = this.binding.substring(this.binding.lastIndexOf(".") + 1 , this.binding.length());
                char[] stringArray = entityName.toCharArray();
                stringArray[0] = Character.toUpperCase(stringArray[0]);
                entityName = new String(stringArray);
            }
        }
    }

    public Layout(String type, boolean fill, String id, String label, String orientation, String asGroupBox, String style, String styleClass, String alignment, String render, String renderedEl, String binding) {
        this(type, id, label, style, styleClass, render, renderedEl, binding);
        this.orientation = orientation;
        this.asGroupBox = asGroupBox;
        this.alignment = alignment;

        if (orientation != null && orientation.equals("vertical")) {
            clazz = "layout vertical";
        } else {
            clazz = "layout horizontal";
        }

        if (fill) {
            clazz += " fill";
        }

        if (alignment != null && !alignment.isEmpty()) {
            clazz += ' ' + alignment;
        }

        if (styleClass != null && !styleClass.isEmpty()) {
            clazz += ' ' + styleClass;
        }
    }

    public T getChildren(String key) {
        return children.get(key);
    }

    public void addChildren(String key, T child) {
        children.put(key, child);

        if (child instanceof Layout) {
            Layout childLayout = (Layout)child;
            childLayout.parent = this;
        }
    }

    public void addSortedChildren(T child) {
        sortedChildren.add(child);
    }

//    public String toString() {
//        final StringBuilder result = new StringBuilder();
//        result.append("<");
//        result.append(type);
//
//
//        if (!(this instanceof DataGridColumn)) { //label of column goes into header
//            appendAttribute(result, "id", id);
//            appendAttribute(result, "label", label);
//            appendAttribute(result, "binding", binding);
//        }
//
//        appendAttribute(result, "orientation", orientation);
//        appendAttribute(result, "asGroupBox", asGroupBox);
//
////        if (style != null) {
////            appendObject(result, "style", "{" + style2javascript(style) + "}");
////        }
//        if (style != null) {
//            appendObject(result, "style",  style);
//        }
//
//        if (styleClass != null && styleClass.contains("'")) {
//            appendObject(result, "styleClass", styleClass);
//        } else {
//            appendAttribute(result, "styleClass", styleClass);
//        }
//
//        appendAttribute(result, "alignment", alignment);
//
//        appendRender(result, render, renderedEl);
//
//        if (sortedChildren.isEmpty() && binding == null) {
//            result.append("/>");
//        } else {
//            result.append(">\n");
//
//            if (!sortedChildren.isEmpty()) {
//                for (Object child : sortedChildren) {
//                    appendIndentation(result);
//                    result.append("\t");
//                    result.append(child.toString());
//                    result.append("\n");
//                }
//            } else {
//                if (this instanceof DataGridColumn && binding != null) {
//                    Widget label = new Widget("Label",null,null,binding,null,null,null,null,null,null,this,null,null,null,null,null,null,null,null);
//                    appendIndentation(result);
//                    result.append("\t");
//                    result.append(label.toString());
//                    result.append("\n");
//                }
//            }
//
//            appendIndentation(result);
//            result.append("</");
//            result.append(type);
//            result.append(">");
//        }
//
//        return result.toString();
//    }
}