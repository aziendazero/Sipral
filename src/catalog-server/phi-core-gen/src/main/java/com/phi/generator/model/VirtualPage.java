package com.phi.generator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse pages parameter of VirtualPage mmgp
 * Example:
 * /MOD_home/CORE/FORMS/vp_segnalazioni_filters_workaccident@function.hasCodeIn(ProtocolloAction.equal['uos.area'].code,'WORKACCIDENT');
 * /MOD_home/CORE/FORMS/vp_segnalazioni_filters_workdisease@function.hasCodeIn(ProtocolloAction.equal['uos.area'].code,'WORKDISEASE');
 * Created by Alex on 13/02/2017.
 */
public class VirtualPage extends BaseComponent {

    private Map<String,String> pages = null;
    public List<Import> imports = null;
    public List<String> state = null;

    private String jollyParameter;

    public VirtualPage(String id, String pages, String jollyParameter, Layout parent) {
        super(null, id, null, parent);

        if (pages != null) {
            this.pages = new HashMap<>();
            this.imports = new ArrayList<>();
            String pageSplit[] = pages.split(";");
            for (String page : pageSplit) {
                String[] pageAndCondition = page.split("@");
                int formIndex = pageAndCondition[0].lastIndexOf("/");
                String path = pageAndCondition[0].substring(0, formIndex);
                String form = pageAndCondition[0].substring(formIndex+1);
                this.imports.add(new Import(form, path));

                String elCondition = null;
                if (pageAndCondition.length == 2) {
                    elCondition = el2javascript(pageAndCondition[1]);
                    state = parseElVariables(elCondition);
                }
                this.pages.put(form, elCondition);
            }
        }

        this.jollyParameter = jollyParameter;
    }

    public Map<String, String> getPages() {
        return pages;
    }

    public String getJollyParameter() {
        return jollyParameter;
    }

//    public String toString() {
//        final StringBuilder result = new StringBuilder();
//
//        result.append("<VirtualPage");
//
//        appendAttribute(result, "id", id);
//
//        if (pages == null) {
//            result.append("/>\n");
//        } else {
//            result.append(" pages={(empty, functions) => {\n");
//
//            for (String page : pages.keySet()) {
//                String javascriptCondition = pages.get(page);
//                if (javascriptCondition != null) {
//                    appendIndentation(result, 1);
//                    result.append("try{if (" + pages.get(page) + ") {\n");
//                    appendIndentation(result, 2);
//                    result.append("return <");
//                    result.append(capitalize(page));
//                    result.append("/>\n");
//                    appendIndentation(result, 1);
//                    result.append("}} catch(e){}\n");
//                } else {
//                    appendIndentation(result, 1);
//                    result.append(capitalize(page));
//                }
//            }
//            appendIndentation(result);
//            result.append("}}/>");
//        }
//        return result.toString();
//    }

}