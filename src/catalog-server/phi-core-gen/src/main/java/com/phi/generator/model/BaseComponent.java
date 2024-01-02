package com.phi.generator.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base component, extended by all widgets and layouts
 * Created by Alex on 20/01/2017.
 */
public abstract class BaseComponent {

    protected String type;
    protected String id;
    protected String binding;

    Layout parent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    BaseComponent() {

    }

    BaseComponent(String type, String id, String binding, Layout parent) {
        this.type = type;
        this.id = id;
        this.binding = binding;
        this.parent = parent;
    }

    static String el2javascript(String javaEl) {
        String javascript = javaEl;

        //Java el
        javascript = javascript.replaceAll(" and ", " && ");
        javascript = javascript.replaceAll(" or ", " || ");
        javascript = javascript.replaceAll("(^|\\(|\\s)not ", "$1! ");
        javascript = javascript.replaceAll(" eq ", " == ");
        javascript = javascript.replaceAll(" ne ", " != ");

        javascript = javascript.replaceAll(" lt ", " < ");
        javascript = javascript.replaceAll(" gt ", " > ");
        javascript = javascript.replaceAll(" ge ", " <= ");
        javascript = javascript.replaceAll(" le ", " >= ");

        javascript = javascript.replaceAll(" div ", " / ");
        javascript = javascript.replaceAll(" mod ", " % ");

        javascript = javascript.replaceAll("(^|!|\\s)empty ([^\\s]+)($|\\s)", "$1empty($2)$3 ");

        javascript = javascript.replaceAll("\\.get\\('(\\w*)'\\)", "\\['$1'\\]");

        javascript = javascript.replaceAll("\\.equals\\(([^\\)]*)\\)", " == $1");

        //phi
        javascript = javascript.replaceAll("(^|\\(|\\s|,)function\\.", "$1functions\\.");

        //System.out.println("el2javascript >>> " + javaEl + " >>> " + javascript);

        return javascript;
    }

    private static String javascript2java(String javascript) {
        String java = javascript;

        //Java
        //.temporary['linkProtocollo']
        java = java.replaceAll("\\['([^']*)'\\]", ".get(\"$1\")");
        java = java.replaceAll("\\[([^']*)\\]", ".get($1)");
        java = java.replaceAll("'([^']*)'", "\"$1\"");

        //System.out.println("javascript2java >>> " + javascript + " >>> " + java);

        return java;
    }

    public static List<String> parseElVariables(String javaEl) {
        List<String> variables = new ArrayList<>();

        try {

            Expression expression = parseEl(javaEl);

            analyze(expression, variables);

        } catch (Exception e) {
            System.err.println("Error parseElVariables " + javaEl + " error " + e.getMessage());
            //throw e;
        }

        return variables;
    }

    public static List<String> parseElVariables(Expression expression) {
        List<String> variables = new ArrayList<>();
        try {
            analyze(expression, variables);
        } catch (Exception e) {
            System.err.println("Error parseElVariables " + expression + " error " + e.getMessage());
        }

        return variables;
    }

    public static Expression parseEl(String javaEl) {
        String javascript = el2javascript(javaEl);
        String java = javascript2java(javascript);
        Expression expression = null;

        try {

            expression = JavaParser.parseExpression(java);

        } catch (Exception e) {
            System.err.println("Error parseEl " + java + " error " + e.getMessage());
            throw e;
        }

        return expression;
    }

    private static void analyze(List<Node> c, List<String> variables) {
        for (Node n : c) {
            analyze(n, variables);
        }
    }

    private static void analyze(Node n, List<String> variables) {
        if (n instanceof SimpleName) {
            SimpleName smplNme = (SimpleName) n;
            variables.add(smplNme.getIdentifier());
        } else if (n instanceof NameExpr) {
            NameExpr nme = (NameExpr) n;
            variables.add(nme.getName().getIdentifier());
        } else if (n instanceof MethodCallExpr) {
            MethodCallExpr mthd = (MethodCallExpr) n;
            List<Node> chlds = new ArrayList<>();
            if (!mthd.getScope().isPresent()) {
                // chlds.add(mthd.getName());
                chlds.addAll(mthd.getArguments());
            } else {
                chlds.add(mthd.getScope().get());
                chlds.addAll(mthd.getArguments());
            }
            analyze(chlds, variables);
        }  else if (n instanceof FieldAccessExpr) {
            FieldAccessExpr fldA = (FieldAccessExpr) n;
            analyze(fldA.getChildNodes().get(0), variables);
        }  else if (n instanceof ArrayAccessExpr) {
            ArrayAccessExpr arrA = (ArrayAccessExpr) n;
            variables.add(arrA.getName().toString());
        } else if (n instanceof BinaryExpr) {
            BinaryExpr bnry = (BinaryExpr) n;
            analyze(bnry.getChildNodes(),variables);
        } else if (n instanceof UnaryExpr) {
            UnaryExpr unry = (UnaryExpr) n;
            analyze(unry.getChildNodes(),variables);
        }
    }

    String style2javascript(String style) {
        String javascript = style;

        javascript = javascript.replaceAll("(\\w+)\\s?:\\s?([\\w%]+)", "$1:'$2'");
        javascript = javascript.replaceAll("\\-(.)", "$1");//FIXME UPPERCASE!
        javascript = javascript.replaceAll(";", ",");
        javascript = javascript.replaceAll(",$", "");

        return javascript;
    }

//    private static final Pattern pActions = Pattern.compile("\\w*Action\\b");
//
//    List<String> findActions(String javaEl) {
//        List<String> allMatches = new ArrayList<>();
//        Matcher m = pActions.matcher(javaEl);
//        while (m.find()) {
//            allMatches.add(m.group());
//        }
//        return allMatches;
//    }

//    void appendRender(StringBuilder result, String render, String renderedEl) {
//        appendAttribute(result, "render", render);
//
//        if (renderedEl != null && !renderedEl.isEmpty()) {
//            //parseElVariables(renderedEl);
//            //String javascriptCondition = el2javascript(renderedEl);
//            //appendObject(result, "renderedEL", "() => (" + javascriptCondition + ")");
//            appendObject(result, "renderedEL", renderedEl);
//        }
//    }
//
//    void appendAttribute(StringBuilder sb, String name, Serializable value) {
//        if (value != null) {
//            sb.append(" ");
//            sb.append(name);
//            sb.append("=\"");
//            sb.append(value);
//            sb.append("\"");
//        }
//    }
//
//    void appendObject(StringBuilder sb, String name, Serializable value) {
//        if (value != null) {
//            sb.append(" ");
//            sb.append(name);
//            sb.append("={");
//            sb.append(value);
//            sb.append("}");
//        }
//    }
//
//
//    void appendIndentation(StringBuilder sb, int add) {
//        for (int i = 0; i < getDepth() + add; i++) {
//            sb.append("\t");
//        }
//    }
//
//    void appendIndentation(StringBuilder sb) {
//        for (int i = 0; i < getDepth(); i++) {
//            sb.append("\t");
//        }
//    }

    public int getDepth() {
        int depth = 0;
        BaseComponent l = this;
        while (l.parent != null) {
            l = l.parent;
            depth++;
            if (l instanceof DataGrid && !(l instanceof TabbedPanel)) {
                depth = depth + 2;
            }
        }
        return depth;
    }

    static String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        char c[] = string.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }


    private static final Pattern ptrnKebabCase = Pattern.compile("\\-(\\w)");
    /**
     * Converts kebab-case to calmelCase
     * @param kebabcase kebab-case
     * @return calmelCase
     */
    static String kebabToCamel(String kebabcase) {
        Matcher m = ptrnKebabCase.matcher(kebabcase);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sb.append(kebabcase.substring(last, m.start()));
            sb.append(m.group(1).toUpperCase());
            last = m.end();
        }
        sb.append(kebabcase.substring(last));

        return sb.toString();
    }
}