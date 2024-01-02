package com.phi.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Form
 * Created by Alex on 18/11/2016.
 */
public class Form extends Layout {

    private String relativePath;
    private String relativePathToRoot = "";
    private List<Import> imports = new ArrayList<>();
    private List<String> variables = new ArrayList<>();
    private List<String> methods = new ArrayList<>();

    public String getRelativePath() {
        return relativePath;
    }

    public String getRelativePathToRoot() {
        return relativePathToRoot;
    }

    public List<Import> getImports() {
        return imports;
    }

    public List<String> getVariables() {
        return variables;
    }

    public List<String> getMethods() {
        return variables;
    }

    public Form() {
        super();
    }

    public Form(String relativePath) {
        super("Form");
        this.relativePath = relativePath;

        int count = relativePath.length() - relativePath.replace("/", "").length();
        for (int i = 0; i<count; i++) {
            relativePathToRoot = "../" + relativePathToRoot;
        }
        relativePathToRoot = relativePathToRoot.substring(0,relativePathToRoot.length()-1);
    }

    public void addImports(List<Import> imports) {
        this.imports.addAll(imports);
    }

    public void addVariable(String variable) {
        if (variable.equals("empty") || variable.equals("Param") || variable.equals("functions")) {
            //SKIP
        } else if (!this.variables.contains(variable)) {
            this.variables.add(variable);
        }
    }

    public void addVariables(List<String> variables) {
        if (variables != null) {
            for (String variable : variables) {
                addVariable(variable);
            }
        }
    }

    public void addMethod(String method) {
        if (method != null) {
            this.methods.add(method);
        }
    }

//    public String toString() {
////        String formName = "";
////        if (fileName != null) {
////            formName = fileName.substring(0, fileName.lastIndexOf("."));
////        }
//        final StringBuilder result = new StringBuilder();
//        result.append("import React from 'react';\n");
//        result.append("import { connect } from 'react-redux';\n");
//        result.append("import { reduxForm } from 'redux-form';\n");
//        result.append("import {Button,ButtonAdd,ButtonBack,ButtonHome,ButtonPDF,ButtonRefresh,ButtonSave,ButtonSearch,TableConversionWidget,CheckBox,ComboBox,DataGrid,DataGridColumn,DataGridHeader,DgBody,Form,GroupCheckBox,Interval,JollyWidget,Label,Layout,Link,ListBox,PictureBox,MonthCalendar,RadioGroup,TabbedPanel,TextArea,TextBox,TextSuggestionBox,VirtualPage} from '");
//        //result.append(getWidgetRelativePath());
//        result.append(relativePathToRoot);
//        result.append("/../app/widgets");
//        result.append("';\n");
//        result.append("import FunctionsBean, {isEmpty} from '");
//        result.append(relativePathToRoot);
//        result.append("/../app/widgets/actions/FunctionsBean.js';\n");
//        for (Import importz : imports) {
//            //result.append(importz);
//            result.append("import " + capitalize(importz.form) + " from '" + relativePathToRoot + importz.path + "/" + importz.form + "';\n");
//        }
//        result.append("\n");
//        result.append("class ");
//        result.append(formName);
//        result.append(" extends React.Component {\n");
//        result.append("    render() {\n");
//
//        //const { xAction, yAction, ...} = this.initialValues;
//        if (variables != null && !variables.isEmpty()) {
//            appendIndentation(result, 1);
//            result.append("const {");
//            for (String entry : variables) {
//                result.append(entry + ",");
//            }
//            result.setLength(result.length() - 1);
//            result.append("} = this.props.initialValues;\n");
//        }
//        appendIndentation(result, 1);
//        result.append("let functions = FunctionsBean;\n");
//        appendIndentation(result, 1);
//        result.append("let empty = isEmpty;\n");
//        appendIndentation(result, 1);
//        result.append("let Param = this.props.param;\n");
//
//        result.append("        return (\n");
//        result.append(super.toString());
//
//        result.append("\n");
//        result.append("        );\n");
//        result.append("    }\n");
//        result.append("}\n");
//
//        //Only redux-form
////        f_search_employee_MULTI = reduxForm({
////                form: 'f_search_employee_MULTI'
////        })(f_search_employee_MULTI);
//
////        result.append("export default reduxForm({\n");
////        result.append("    form: '");
////        result.append(formName);
////        result.append("'\n})(");
////        result.append(formName);
////        result.append(");");
//
//
//
//        //Redux and redux-form
////        f_search_employee_MULTI = reduxForm({
////                form: 'f_search_employee_MULTI'
////        })(f_search_employee_MULTI);
////
////        export default connect(
////                state => ({initialValues: state.process.lists})
////        )(f_search_employee_MULTI);
//
//        result.append(formName);
//        result.append(" = reduxForm({\n");
//        result.append("    form: '");
//        result.append(formName);
//        result.append("',\n");
//
//        //reinitialize the form component with new "pristine" values:
//        // see: http://redux-form.com/6.5.0/examples/initializeFromState/
//        result.append("    enableReinitialize: true");
//
//        result.append("\n})(");
//        result.append(formName);
//
//        result.append(");\n");
//
//        result.append("export default connect(\n");
//        result.append("    state => ({initialValues: state.process.data, param: state.process.param})\n");
//        result.append(")(");
//        result.append(formName);
//        result.append(");");
//
//        return result.toString();
//    }
}