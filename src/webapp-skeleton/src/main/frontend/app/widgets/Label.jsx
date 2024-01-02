import React from 'react';
import { connect } from 'react-redux'
//import TextBox from './TextBox';

Object.byString = function(o, s) {
    s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    s = s.replace(/^\./, '');           // strip a leading dot
    var a = s.split('.');
    for (var i = 0, n = a.length; i < n; ++i) {
        var k = a[i];
        if (k in o) {
            o = o[k];
            if (o === null) {
                return null;
            }
        } else {
            return null;
        }
    }
    return o;
};

class Label extends React.Component {
    render() {

        var render = this.props.render || true;
        var renderedEL = this.props.renderedEL;

        if (renderedEL != undefined) {
            //let functions = FunctionsBean;
            //let empty = isEmpty;
            try {
                render = renderedEL();
            } catch (e) {
                //render = false;
                console.log(e.message + " : " + this.props.id + " renderedEl:");
                console.log(renderedEL);
            }
        }
        if (render) {
            //var label = <TextBox type="span" disabled="true" {...this.props}/>;


            //var formName = this._reactInternalInstance._context._reduxForm.form;

            //let value = this.props.values[];

            let value = '';

            if (this.props.values != undefined && this.props.binding != null && this.props.binding != "") {
                value = Object.byString(this.props.values, this.props.binding);
                if (value instanceof Date) {
                    if (this.props.dateTimePatternLength != null) {
                        if (this.props.dateTimePatternLength.startsWith('2')) {
                            //value = value.toString('dd-MM-yyyy HH:mm');
                            value = value.toLocaleDateString("it-IT")
                        } else {
                            value = value.toDateString();
                        }
                    } else {
                        value = value.toDateString();
                    }
                } else if (value != null && typeof value === 'object') {
                    //FIXME object to string!!!
                    value = JSON.stringify(value);
                }
            } else if (this.props.children != null) {
                value = this.props.children;
            }

            var label = <span className={this.props.styleClass}>{value}</span>;

            if (this.props.widgetLabel != null) {
                return (
                    <div className="label">
                        <label htmlFor={this.props.id}>{this.props.widgetLabel}</label>
                        {label}
                    </div>
                );
            } else {
                return (label);
            }
        } else {
            return null;
        }
    }
}

export default connect(
    state => ({values: state.process.data})
)(Label);