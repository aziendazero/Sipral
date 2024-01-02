package com.phi.generator.model;

import com.phi.generator.PhiGeneratorMojo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic widget
 * Created by Alex on 18/11/2016.
 */
public class Widget extends BaseComponent {

    protected String value;
    private String bindingHigh;//Interval
    private String widgetLabel;
    private Integer x;
    private Integer y;
    protected Integer width;
    protected Integer height;

    protected String styleClass;
    private String mnemonicName;
    private String tooltip;

    //domain
    private String domain;
    //Combo...
    private String listElementsExpression;

    //JollyWidget
    private String customCode;
    private static final Pattern xmlCommentsPtrn = Pattern.compile("<!--.*-->", Pattern.DOTALL);
    private static final Pattern tagScriptPtrn = Pattern.compile("<script[^>]*+>(.*)</script>", Pattern.DOTALL);

    private String required;
    private String rendered;
    private String disabled;

    private String dateTimePatternLength;

    private String iconClass;
    private String valueEL;

    // Non phi model attribute
    private String dgBodyEntityName;
    private String reduxFormBinding;

    private String dateTimeFormat;
    private String dateTimePattern;

    private Boolean todayIsDefaultTime;

    private String converter;

    private String emptyField;

    private String jollyParameter;

    private String layout;

    //Slider
    private String minimum;
    private String maximum;

    private String defaultValue;

    public String getValue() {
        return value;
    }

    public String getBindingHigh() {
        return bindingHigh;
    }

    public String getWidgetLabel() {
        return widgetLabel;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getMnemonicName() {
        return mnemonicName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getDomain() {
        return domain;
    }

    public String getListElementsExpression() {
        return listElementsExpression;
    }

    public String getCustomCode() {
        return customCode;
    }

    public String getRequired() {
        return required;
    }

    public String getRendered() {
        return rendered;
    }

    public String getDisabled() {
        return disabled;
    }

    public String getDgBodyEntityName() {
        return dgBodyEntityName;
    }

    public String getReduxFormBinding() {
        return reduxFormBinding;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public String getDateTimePatternLength() {
        return dateTimePatternLength;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public Boolean getTodayIsDefaultTime() {
        return todayIsDefaultTime;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getValueEL() {
        return valueEL;
    }

    public String getConverter() {
        return converter;
    }

    public String getEmptyField() {
        return emptyField;
    }

    public String getJollyParameter() {
        return jollyParameter;
    }

    public String getLayout() {
        return layout;
    }

    public String getMaximum() {
        return maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Widget(Layout parent, String type, String binding) {
        this.parent = parent;
        this.type = capitalize(type);
        if (binding != null && !binding.isEmpty()) {
            this.binding = binding;
        }

        if (parent instanceof DataGridColumn && parent.parent instanceof DataGrid) {

            this.dgBodyEntityName = ((DataGrid) parent.parent).getEntityName();
            //In DataGrid change binding:
            //Replace phi binding into react redux-form ArrayField name: Employee.name.fam => Employee + '.name.fam'
            if (binding != null) {
                this.reduxFormBinding = pathInDatagrid(binding, parent.parent.binding, dgBodyEntityName);
            }

        }
    }

    public Widget(Layout parent, String type, String id, String value, String binding, String bindingHigh, String widgetLabel, String listElementsExpression,
                  String styleClass, String mnemonicName, String tooltip,String customCode,
                  String required, String requiredEL, String render, String renderedEl, String disabled, String disabledEL,
                  String dateTimeFormat, String dateTimePatternLength, Boolean todayIsDefaultTime, String iconClass, String valueEL, String converter,
                  String emptyField, String jollyParameter, String layout, String minimum, String maximum, String defaultValue) {

        this(parent, type, binding);

        this.id = id;
        this.value = value;

        this.bindingHigh = bindingHigh;

        this.widgetLabel = widgetLabel;
        this.listElementsExpression = listElementsExpression;
        this.styleClass = styleClass;
        this.mnemonicName = mnemonicName;
        this.tooltip = tooltip;

        if (this.listElementsExpression == null || this.listElementsExpression.isEmpty()) {
            this.domain = PhiGeneratorMojo.getDomain(binding);
        }

        //JollyWidget
        if (customCode != null) {
            //Remove
            this.value = customCode.replaceAll("<!\\[CDATA\\[", "");
            this.value = this.value.replaceAll("\\]\\]>", "");
            this.value = this.value.replaceAll("h:", "");
            this.value = this.value.replaceAll("f:", "");
            this.value = this.value.replaceAll("jstl:", "");
            this.value = this.value.replaceAll("'?#\\{([^\\}]*)\\}'?", "$1");

            //Skip comments
            this.value = xmlCommentsPtrn.matcher(this.value).replaceAll("");

            //Extract script tag
            Matcher scriptMatcher = tagScriptPtrn.matcher(this.value);
            if (scriptMatcher.find()) {
                this.customCode = scriptMatcher.group(1);
                this.customCode = el2javascript(this.customCode);
            }
            this.value = scriptMatcher.replaceAll("");

            if (customCode.contains("a4j:")) {
                System.err.println("Error Widget " + type + "with id:" + id + " contains a4j: " + customCode + " SKIPPING!");
                this.value = this.value.replaceAll("a4j:", "");
            }
        }
        //JollyWidget end

        if ("yes".equals(required)) {
            this.required = "true";
        } else if (requiredEL != null && !requiredEL.isEmpty()) {
            this.required = el2javascript(requiredEL);
        }

        if ("no".equals(render)) {
            this.rendered = "false";
        } else if (renderedEl != null && !renderedEl.isEmpty()) {
            this.rendered = el2javascript(renderedEl);
        }

        if ("yes".equals(disabled)) {
            this.disabled = "true";
        } else if (disabled != null && !disabled.isEmpty()) { // some widgets have el into disabled
            this.disabled = el2javascript(disabled);
        } else if (disabledEL != null && !disabledEL.isEmpty()) { // other have el into disabledEl
            this.disabled = el2javascript(disabledEL);
        }

        this.dateTimeFormat = dateTimeFormat;

        this.dateTimePatternLength = dateTimePatternLength;

        if (this.dateTimePatternLength == null) {
            this.dateTimePatternLength = "2";
        } else {
            if (this.dateTimeFormat == null) {
                this.dateTimeFormat = "Date only";
            }
        }

        if ("Time only".equals(this.dateTimeFormat)) {
            this.dateTimePattern = "shortTime";
//            if (this.dateTimePatternLength.startsWith("1")) {
//                this.dateTimePattern = "H:mm";
//            } else {
//                this.dateTimePattern = "HH:mm";
//            } // FIXME implement 3,4,5!!
        } else if ("Date only".equals(this.dateTimeFormat)) {
            this.dateTimePattern = "shortDate";
//            if (this.dateTimePatternLength.startsWith("1")) {
//                this.dateTimePattern = "d/M/yy";
//            } else {
//                this.dateTimePattern = "dd/MM/yyyy";
//            } // FIXME implement 3,4,5!!
        } else if ("Date and Time".equals(this.dateTimeFormat)) {
            this.dateTimePattern = "short";
//            if (this.dateTimePatternLength.startsWith("1")) {
//                this.dateTimePattern = "d/M/yy H:mm";
//            } else {
//                this.dateTimePattern = "dd/MM/yyyy HH:mm";
//            } // FIXME implement 3,4,5!!
        }

        this.todayIsDefaultTime = todayIsDefaultTime;

        this.iconClass = iconClass;
        this.valueEL = valueEL;

        if ("comboBox".equals(type) || "radioGroup".equals(type) || "groupCheckBox".equals(type)) {
            if (converter == null) {
                //Default
                this.converter = "Code Value Converter";
            } else if (!converter.isEmpty()) {
                this.converter = converter;
            }
        } else {
            this.converter = converter;
        }
        this.emptyField = emptyField;
        this.jollyParameter = jollyParameter;

        if ((this.type.equals("RadioGroup") || this.type.equals("GroupCheckBox")) && layout == null) { //Radio group layout default is pageDirection = horizontal
            this.layout = "vertical";
        }

        this.minimum = minimum;
//        if ("Slider".equals(this.type) && this.minimum == null) {
//            this.minimum = "0";
//        }
        if (this.minimum != null && this.minimum.endsWith(".0")) {
            this.minimum = this.minimum.substring(0, this.minimum.length() - 2);
        }

        this.maximum = maximum;
//        if ("Slider".equals(this.type) && this.maximum == null) {
//            this.maximum = "100";
//        }
        if (this.maximum != null && this.maximum.endsWith(".0")) {
            this.maximum = this.maximum.substring(0, this.maximum.length() - 2);
        }

        this.defaultValue = defaultValue;
    }

//    public String toString() {
//        final StringBuilder result = new StringBuilder();
//
//        result.append("<");
//        result.append(type);
//
////        String dgBodyEntityName = null;
////
//        if (parent instanceof DataGridColumn && parent.parent instanceof DataGrid) {
////            //In DataGrid change binding:
////            //Replace phi binding into redux-form ArrayField name: Employee.name.fam => Employee + '.name.fam'
////            String dgBodyBinding = parent.parent.binding;
////            dgBodyEntityName = ((DataGrid) parent.parent).getEntityName();
//////            if (binding != null && dgBodyBinding != null) {
//////                if (binding.contains(dgBodyEntityName)) {
//////                    String tableBinding = "";
//////                    Pattern p = Pattern.compile("(.*[^\\w]*)" + dgBodyEntityName + "([^\\w].*)");
//////                    Matcher m = p.matcher(binding);
//////                    if (m.matches()) {
//////                        if (!m.group(1).isEmpty()) {
//////                            tableBinding = "\"" + m.group(1) + "\" + ";
//////                        }
//////                        tableBinding += dgBodyEntityName  + " + \"" + m.group(2)+ "\"";
//////                    }
//////                    appendObject(result, "binding", tableBinding);
//////                } else {
//////                    System.err.println("Error binding without " + dgBodyEntityName + " in " + binding + " data grid binding: " + dgBodyBinding);
//////                }
//////            } else {
//////                System.err.println("Error dataGrid " + parent.parent.id + " has no binding!");
//////            }
////            String tableBinding = pathInDatagrid(binding, dgBodyBinding, dgBodyEntityName);
//            appendObject(result, "binding", binding);
////
////            //renderedEl = pathInDatagrid(renderedEl, dgBodyBinding, dgBodyEntityName);
////
//        } else {
//            appendAttribute(result, "binding", binding);
//            appendAttribute(result, "bindingHigh", bindingHigh); //Interval
//        }
//
//        appendAttribute(result, "id", id);
//        appendAttribute(result, "mnemonicName", mnemonicName);
//        appendAttribute(result, "widgetLabel", widgetLabel);
//
//        appendAttribute(result, "x", x);
//        appendAttribute(result, "y", y);
//        appendAttribute(result, "width", width);
//        appendAttribute(result, "height", height);
//
//        if (styleClass != null && styleClass.contains("'")) {
//            appendObject(result, "styleClass", styleClass);
//        } else {
//            appendAttribute(result, "styleClass", styleClass);
//        }
//
//        appendAttribute(result, "tooltip", tooltip);
//
//        appendAttribute(result, "domain", domain);
//
//        //Combo...
//        appendAttribute(result, "listElementsExpression", listElementsExpression);
//
//        //JollyWidget...
//        if (customCode != null && !customCode.isEmpty()) {
//            appendObject(result, "customCode", "() => {" + customCode + "}");
//        }
//
//        appendRender(result, render, renderedEl);
//
//        appendAttribute(result, "dateTimePatternLength", dateTimePatternLength);
//
//        if ("Link".equals(type) || type.startsWith("Button")) {
//            //Redux-form handleSubmit
//            if (dgBodyEntityName != null) {
//                appendObject(result, "inject", dgBodyEntityName);
//            }
//            result.append(" handleSubmit={this.props.handleSubmit}");
//        }
//
//        if (value != null && !value.isEmpty()) {
//            result.append(">");
//            result.append(value);
//            result.append("</");
//            result.append(type);
//            result.append(">");
//        } else {
//            result.append("/>");
//        }
//
//        return result.toString();
//    }

    private String pathInDatagrid(String binding, String dgBodyBinding, String dgBodyEntityName) {
        if (binding != null && dgBodyBinding != null) {
            if (binding.contains(dgBodyEntityName)) {
                String tableBinding = "";
                Pattern p = Pattern.compile("(.*[^\\w]*)" + dgBodyEntityName + "([^\\w].*)");
                Matcher m = p.matcher(binding);
                if (m.matches()) {
                    if (!m.group(1).isEmpty()) {
                        tableBinding = "\"" + m.group(1) + "\" + ";
                    }
                    tableBinding += dgBodyEntityName  + "Row + \"" + m.group(2)+ "\"";
                }
                return tableBinding;
            }
        } else {
            System.err.println("Error dataGrid " + parent.parent.id + " has no binding!");
        }
        return null;
    }
}