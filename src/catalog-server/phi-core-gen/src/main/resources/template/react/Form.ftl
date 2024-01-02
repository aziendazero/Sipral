<#import "Macro.ftl" as M>
<#import "Layout.ftl" as L>
<#import "DataGrid.ftl" as D>
<#import "Widget.ftl" as W>
<#import "TabbedPanel.ftl" as T>
<#import "VirtualPage.ftl" as V>
import React from 'react';
import { connect } from 'react-redux';
import { reduxForm } from 'redux-form';
import {Button,ButtonAdd,ButtonBack,ButtonHome,ButtonPDF,ButtonRefresh,ButtonSave,ButtonSearch,TableConversionWidget,CheckBox,ComboBox,DataGrid,DataGridColumn,DataGridHeader,DgBody,Form,GroupCheckBox,Interval,JollyWidget,Label,Layout,Link,ListBox,PictureBox,MonthCalendar,RadioGroup,TabbedPanel,TextArea,TextBox,TextSuggestionBox,VirtualPage} from '${relativePathToRoot}/../app/widgets';
import FunctionsBean, {isEmpty} from '${relativePathToRoot}/../app/widgets/actions/FunctionsBean.js';
<#list imports as imp>
import ${imp.form?cap_first} from '${relativePathToRoot}${imp.path}/${imp.form}';
</#list>
class ${formName} extends React.Component {
  render() {
<#if variables?has_content>
  const {<#list variables as v>${v}<#if v_has_next>,</#if></#list>} = this.props.initialValues;
</#if>
  let functions = FunctionsBean;
  let empty = isEmpty;
  let Param = this.props.param;
  return (
        <@L.Layout l=.vars/>
    );
  }
}
${formName} = reduxForm({
  form: '${formName}',
  enableReinitialize: true
})(${formName});
export default connect(
  state => ({initialValues: state.process.data, param: state.process.param})
)(${formName});