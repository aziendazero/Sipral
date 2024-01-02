package com.phi.generator.parser;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.phi.generator.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.Stack;

/**
 * Parse a PHI form mmgp and return object representation
 * Created by Alex on 18/11/2016.
 */
public class SaxParser {

    //Stack of elements
    private static Stack<BaseComponent> elementStack;

//    private static String printStack() {
//        Iterator<BaseComponent> iter = elementStack.iterator();
//        StringBuilder sb = new StringBuilder();
//        while (iter.hasNext()){
//            BaseComponent element = iter.next();
//            sb.append(/*element.type + " " +*/ element.getId() + ", ");
//        }
//        return (sb.toString());
//    }

    private static void stackPush(BaseComponent element) {
        if (element != null) {
            elementStack.push(element);
            //System.out.println(" --> PUSH: " + /*element.type + " " +*/ element.id + "\t\t\t" + printStack());
            //System.out.println(printStack());
        } else {
            System.err.println(" --> PUSH: NULL ELEMENT!!!");
        }
    }

    private static BaseComponent stackPeek() {
        return elementStack.peek();
        //BaseComponent peakedEl = elementStack.peek();
        //System.out.println(" --> PEEK: " + peakedEl.type + " " + peakedEl.id);
        //return peakedEl;
    }

    private static void stackPop() {
        /*BaseComponent poppedEl =*/ elementStack.pop();
        //System.out.println(" --> POP: " /*+ poppedEl.type + " "*/ + poppedEl.id + "\t\t\t" + printStack());
        //System.out.println(printStack());
    }

    public static Form parse(final File f, final String relativePath) {

        elementStack = new Stack<>();

        final Form form = new Form(relativePath);

        System.out.println("Parsing " + f.getName());

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                //reference from notation:Diagram to components:Editor
                String elementRef = null;

                public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

                    if ("xmi:XMI".equalsIgnoreCase(qName)) {
                        //return;
                        //stackPush(form);

                    } else if ("components:Editor".equalsIgnoreCase(qName)) {

                        form.setId(attributes.getValue("formName"));
                        form.setLabel(attributes.getValue("formLabel"));
                        //FIXME formLabelEL
                        stackPush(form);

                    } else if ("layoutRow".equalsIgnoreCase(qName) || "layoutFill".equalsIgnoreCase(qName)
                            || "layoutRowPanelTab".equalsIgnoreCase(qName) || "layoutFillPanelTab".equalsIgnoreCase(qName)
                            || "panelTab".equalsIgnoreCase(qName) || "groupBox".equalsIgnoreCase(qName)) {

                        Layout parent = ((Layout)stackPeek());

                        String label = attributes.getValue("boxLabel");
                        if (label == null) {
                            label = attributes.getValue("tabLabel");
                        }

                        String renderedEL = attributes.getValue("renderedEL");


                        boolean fill = false;
                        if ("layoutFill".equalsIgnoreCase(qName) || "layoutFillPanelTab".equalsIgnoreCase(qName)) {
                            fill = true;
                        }

                        Layout newLayout = new Layout("Layout", fill, attributes.getValue("name"), label,
                                attributes.getValue("orientation"), attributes.getValue("asGroupBox"),
                                attributes.getValue("style"), attributes.getValue("styleClass"),
                                attributes.getValue("alignment"), attributes.getValue("render"), renderedEL, attributes.getValue("binding"));


                        if (renderedEL != null && !renderedEL.isEmpty()) {
                            form.addVariables(Layout.parseElVariables(renderedEL));
                        }

                        //FIXME expandMode, style
                        //FIXME layout*PanelTab: tabLabel

                        parent.addChildren(attributes.getValue("xmi:id"), newLayout);
                        stackPush(newLayout);

                    } else if ("dataGrid".equalsIgnoreCase(qName) || "dataGridGB".equalsIgnoreCase(qName)) {
                        String binding = attributes.getValue("binding");

                        DataGrid dataGrid = new DataGrid(attributes.getValue("name"), attributes.getValue("value"),
                                binding, attributes.getValue("caption"), attributes.getValue("selectableRow"),
                                attributes.getValue("style"), attributes.getValue("styleClass"),
                                attributes.getValue("render"), attributes.getValue("renderedEL"), attributes.getValue("jollyParameter"));
                        ((Layout)stackPeek()).addChildren(attributes.getValue("xmi:id"), dataGrid);
                        stackPush(dataGrid);

                        if (binding != null) {
                            form.addVariables(Layout.parseElVariables(binding));
                            form.addVariable(dataGrid.getEntityName());
                        }

                    } else if ("dataGridLabelDG".equalsIgnoreCase(qName)) { //DataGridColumn
                        DataGrid dataGrid = ((DataGrid) stackPeek());

                        DataGridColumn dataGridColumn = new DataGridColumn(attributes.getValue("name"),
                                attributes.getValue("value"), attributes.getValue("binding"),
                                attributes.getValue("style"), attributes.getValue("styleClass"),
                                attributes.getValue("renderedEL"), attributes.getValue("sortable"));

                        dataGrid.addChildren(attributes.getValue("xmi:id"), dataGridColumn);
                        stackPush(dataGridColumn);

                    } else if ("tabbedPanel".equalsIgnoreCase(qName)) {

                        String binding = attributes.getValue("binding");

                        TabbedPanel tabbedPanel = new TabbedPanel(attributes.getValue("name"), attributes.getValue("value"),
                                binding, "caption", "true",
                                attributes.getValue("style"), attributes.getValue("styleClass"),
                                attributes.getValue("render"), attributes.getValue("renderedEL"), attributes.getValue("jollyParameter"));
                        ((Layout) stackPeek()).addChildren(attributes.getValue("xmi:id"), tabbedPanel);
                        stackPush(tabbedPanel);

                        if (binding != null) {
                            form.addVariables(Layout.parseElVariables(binding));
                            form.addVariable(tabbedPanel.getEntityName());
                        }

                    } else if ("notation:Diagram".equalsIgnoreCase(qName)) {

                        //form.fileName = attributes.getValue("name"); //Not always the correct one
                        form.setFileName(f.getName());

                    } else if ("children".equalsIgnoreCase(qName)) {

                        String elementId = attributes.getValue("element");
                        if (elementId != null) {

                            elementRef = elementId;

                            Object element = stackPeek();
                            if (element instanceof Layout) {
                                BaseComponent widgetObj = ((Layout) element).getChildren(elementRef);
                                if (widgetObj == null) {
                                    System.err.println("Sorting not found element: " + elementRef);
                                } else if (widgetObj instanceof Validator){
                                    //FIXME IMPLEMENT VALIDATION
                                } else {
                                    ((Layout) element).addSortedChildren(widgetObj);
                                }

                                stackPush(widgetObj);
                            } else if (element instanceof Widget) {
                                System.err.println("Error parsing " + f.getName() + " element " + elementId +
                                        " current elementStack " + ((Widget)element).getId() + " isn't Layout!");
                            }

                        } else {
                            stackPush(stackPeek()); //Useless but necessary to pop right
                        }

                    } else if ("layoutConstraint".equalsIgnoreCase(qName)) {
                            Object widgetObj = form.getChildren(elementRef);
                        if (widgetObj instanceof Widget) {
                            Widget widget = (Widget)widgetObj;
                            widget.setX(tryParse(attributes.getValue("x")));
                            widget.setY(tryParse(attributes.getValue("y")));
                            widget.setWidth(tryParse(attributes.getValue("width")));
                            widget.setHeight(tryParse(attributes.getValue("height")));
                        }
                    } else if ("styles".equalsIgnoreCase(qName)) {
                        //ignore
                        //return;
                    } else if ("abstractControl".equalsIgnoreCase(qName)) {
                        Layout currLayout = ((Layout) stackPeek());
                        String type = attributes.getValue("xmi:type");
                        type = type.substring(11, type.length()); //remove components:
                        currLayout.addChildren(attributes.getValue("xmi:id"), new Validator(type, attributes.getValue("maximum")));
                    } else {
                        //WIDGETS
                        Layout currLayout = ((Layout) stackPeek());
                        String type = qName;
                        if ("widgets".equalsIgnoreCase(qName)) {
                            //widgets inside data grid: type isn't in name but in xmi:type
                            //<widgets xmi:type="components:Label"...
                            type = attributes.getValue("xmi:type");
                            type = type.substring(11, type.length()); //remove components:
                        }

                        String binding = attributes.getValue("binding");

                        //WIDGETS IN GROUPBOX: remove GB
                        if(type.endsWith("GB")) {
                            type = type.substring(0, type.length()-2);
                        }

                        //IdBox: convert to TextBox
                        if (qName.startsWith("iDBox")) {
                            type = "TextBox";
                            binding += "['" + attributes.getValue("root") + "']";
                        }

                        if (type.equals("virtualPage")) {
                            VirtualPage vp = new VirtualPage(attributes.getValue("name"), attributes.getValue("pages"), attributes.getValue("jollyParameter"), currLayout);
                            form.addImports(vp.imports);
                            form.addVariables(vp.state);
                            currLayout.addChildren(attributes.getValue("xmi:id"), vp);
                        } else {
                            String renderedEL = attributes.getValue("renderedEL");
                            String listElementsExpression = attributes.getValue("listElementsExpression");
                            String tooltip = attributes.getValue("tooltip");
                            if (tooltip == null || tooltip.isEmpty()) {
                                tooltip = attributes.getValue("alt"); // Buttons have alt instead of tooltip property
                            }
                            Widget widget = new Widget(currLayout, type,
                                    attributes.getValue("name"), attributes.getValue("value"), binding, attributes.getValue("bindingHigh"),
                                    attributes.getValue("widgetLabel"), listElementsExpression,
                                    attributes.getValue("styleClass"), attributes.getValue("mnemonicName"), tooltip,
                                    attributes.getValue("customCode"),
                                    attributes.getValue("required"), attributes.getValue("requiredEL"),
                                    attributes.getValue("render"), renderedEL, attributes.getValue("disabled"), attributes.getValue("disabledEL"),
                                    attributes.getValue("dateTimeFormat"), attributes.getValue("dateTimePatternLength"), "true".equals(attributes.getValue("todayIsDefaultTime")),
                                    attributes.getValue("iconClass"), attributes.getValue("valueEL"), attributes.getValue("converter"),
                                    attributes.getValue("emptyField"), attributes.getValue("jollyParameter"), attributes.getValue("layout"),
                                    attributes.getValue("minimum"), attributes.getValue("maximum"), attributes.getValue("defaultValue"));

                            if (renderedEL != null && !renderedEL.isEmpty()) {
                                form.addVariables(widget.parseElVariables(renderedEL));
                            }
                            if (binding != null && !binding.isEmpty()) {
                                Expression e = widget.parseEl(binding);
                                form.addVariables(widget.parseElVariables(e));
                                if (e instanceof MethodCallExpr) {
                                    form.addMethod(((MethodCallExpr) e).getName().toString());
                                }
                            }
                            if (listElementsExpression != null && !listElementsExpression.isEmpty()) {
                                form.addVariables(widget.parseElVariables(listElementsExpression));
                            }
                            currLayout.addChildren(attributes.getValue("xmi:id"), widget);
                        }
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException {
                    try {

                        if ("layoutRow".equalsIgnoreCase(qName) || "layoutFill".equalsIgnoreCase(qName)
                                || "layoutRowPanelTab".equalsIgnoreCase(qName) || "layoutFillPanelTab".equalsIgnoreCase(qName)
                                || "panelTab".equalsIgnoreCase(qName) || "groupBox".equalsIgnoreCase(qName)) {
                            stackPop();
                        } else if (qName.equalsIgnoreCase("dataGrid") || "dataGridGB".equalsIgnoreCase(qName) || qName.equalsIgnoreCase("dataGridLabelDG")) {
                            stackPop();
                        } else if (qName.equalsIgnoreCase("tabbedPanel")) {
                            stackPop();
                        } else if (qName.equalsIgnoreCase("children")) {
                            stackPop();
                        }

                    } catch (Exception e) {
                        System.err.println("Error parsing " + f.getName() + " element " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {

                }

            };

            saxParser.parse(f, handler);

        } catch (Exception e) {
            System.err.println("Error parsing " + f.getName() + " element " + e.getMessage());
            e.printStackTrace();
        }

        return form;

    }

    private static Integer tryParse(String intStr) {
        if (intStr != null) {
            try {
                return Integer.parseInt(intStr);
            } catch (NumberFormatException e) {
                System.err.println("Error parseInt " + intStr + " " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}