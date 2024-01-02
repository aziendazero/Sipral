import React from 'react';
import { Field, FieldArray, reduxForm } from 'redux-form';

export default class ListBox extends React.Component {
    render() {
        return (
                <FieldArray name={this.props.binding} component={renderBody}/>
        );
    }
}

const renderBody = ({ fields, meta: { touched, error }}) => (
    <select multiple="multiple">
        {fields.map((row , index) =>
        <option value={index}>{row}</option>
        )}
    </select>
);