import React from 'react';
import { connect } from 'react-redux'
import { Field, FieldArray, reduxForm } from 'redux-form';

//FIXME remove
import {Button,ButtonAdd,ButtonHome,CheckBox,ComboBox,DataGrid,DataGridColumn,DataGridHeader,Form,JollyWidget,Label,Layout,Link,MonthCalendar,RadioGroup,TabbedPanel,TextArea,TextBox,TextSuggestionBox,VirtualPage} from '..';

import {inject} from '../../actions';

export default class DgBody extends React.Component {

    // handleClick = (id, mnemonicName, event) => {
    //
    //     //var forms = this.props.form;
    //     const { form, inject } = this.props;
    //
    //     function submit(values, dispatch, props) {
    //
    //         let registeredFields = null;// = form[props.form].registeredFields;
    //
    //         dispatch(manageTask({id, mnemonicName, inject, registeredFields, values}));
    //     }
    //
    //     var executeSubmit = this.props.handleSubmit(submit);
    //     executeSubmit(event);
    // };

    injectEject = (listName, listIndex, event) => {

        function submit(values, dispatch, props) {
            props.dispatch(inject(listName, listIndex));
        }

        var executeSubmit = this.props.handleSubmit(submit);
        executeSubmit(event);

        //this.props.dispatch(inject(listName, listIndex));

        // let trgt = event.target;
        // if (trgt) {
        //     trgt.classList.add("selRow");
        // }
    };
    
    render() {

        let injectedIndex = null; //FIXME
        // if (this.props.values != undefined && this.props.binding != null && this.props.binding != "") {
        //     let list = null;
        //     let injected = null;
        //     list = Object.byString(this.props.values, this.props.binding);
        //     injected = Object.byString(this.props.values, this.props.binding.substring(0,this.props.binding.length - 4));
        //     injectedIndex = list.indexOf(injected);
        // }

        return (
            <FieldArray name={this.props.binding} component={renderBody} dgRow={this.props.children} dgVar={this.props.var} createBody={this.props.createBody} injectEject={this.injectEject} injectedIndex={injectedIndex} className={this.props.selectableRow ? 'selectable' : ''}/>
        );
    }
}

//Button add row:
// <tr>
//     <td>
//         <button type="button" onClick={() => fields.push({})}>Add row</button>
//         {touched && error && <span>{error}</span>}
//     </td>
// </tr>
const renderBody = ({ fields, meta: { touched, error }, dgRow, dgVar, createBody, injectEject, injectedIndex, className }) => (
    <tbody className={className}>
    {
        fields.map((row , index, fieldz) => {
            let entity = fieldz.get(index);
            return createBody(entity, row, index, injectEject, index === injectedIndex);
        }
    )}
    </tbody>
);

// export default connect(
//     state => ({values: state.process.data}) //FIXME Useless, only to access dispatch
// )(DgBody);