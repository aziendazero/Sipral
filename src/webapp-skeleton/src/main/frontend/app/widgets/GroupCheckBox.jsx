import React from 'react';
import { Field, FieldArray, reduxForm } from 'redux-form';

export default class GroupCheckBox extends React.Component {
    render() {
        let widgetLabel = this.props.widgetLabel || this.props.children;
        var input = <FieldArray name={this.props.binding} component={renderBody}/>;
        if (widgetLabel != null) {
            return (
                <div className="grpChkBx">
                    <label htmlFor={this.props.id} className="widget-label">{widgetLabel}</label>
                    {input}
                </div>
            );
        } else {
            return (input);
        }
    }
}

const renderBody = ({ fields, meta: { touched, error }}) => (
    <div>
        {fields.map((row , index) =>
            <input type="checkbox" value={index}>{row}</input>
        )}
    </div>
);