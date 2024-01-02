import React from 'react';
import { Field } from 'redux-form';

// const renderField = ({ input, label, type, meta: { touched, error } }) => (
//     <div>
//         <label>{label}</label>
//         <div>
//             <input {...input} type={type} placeholder={label}/>
//             {touched && error && <span>{error}</span>}
//         </div>
//     </div>
// );

const renderSpan = ({ input, label, type, meta: { touched, error } }) => (
    <span>{input.value}</span>
)

export default class TextBox extends React.Component {
    
    render() {
        
        const type = this.props.type || 'text';
        let component = this.props.component || 'input';
        let widgetLabel = this.props.widgetLabel || this.props.children;

        var input;
        
        if (this.props.binding == null) {
            input = <input type={type}/>
        } else {

            //Replace phi binding into redux-form ArrayField name
            // function replacer(match, p1, p2, offset, string) {
            //     //`${row}.name.giv`
            //     return '${' + p1 + '}' + p2;
            // }
            //
            // var tblBinding = this.props.binding.replace(/([^\.]*)(.*)/, replacer);



            if (type == 'span') {
                component = renderSpan;
            }

            input = <Field component={component} type={type}
                           key={this.props.id}
                           name={this.props.binding}
                           title={this.props.tooltip}
                           className={this.props.styleClass}
                           disabled={this.props.disabled}
                           format={this.props.format}/>
        }

        if (widgetLabel != null) {
            return (
                <div className={'input ' + type}>
                    <label htmlFor={this.props.id} className="widget-label">{widgetLabel}</label>
                    {input}
                </div>
            );
        } else {
            return (input);
        }
    }
}