import React from 'react';
import { connect } from 'react-redux'
import {manageTask} from '../actions';
import FunctionsBean, {isEmpty} from './actions/FunctionsBean.js';
//import {isEmpty} from './actions/FunctionsBean.js';

export default class Link extends React.Component {

    handleClick = (id, mnemonicName, event) => {

        //var forms = this.props.form;
        const { form, inject } = this.props;

        function submit(values, dispatch, props) {

            let registeredFields = null;// = form[props.form].registeredFields;

            dispatch(manageTask({id, mnemonicName, inject, registeredFields, values}));
        }

        var executeSubmit = this.props.handleSubmit(submit);
        executeSubmit(event);
    };

    render() {
        var render = this.props.render || true;
        var renderedEL = this.props.renderedEL; //|| true;
        if (render == 'no') {
            render = false;
        }
        if (renderedEL != undefined) {
            //let functions = FunctionsBean;
            //let empty = isEmpty;
            try {
                render = renderedEL();
            } catch (e) {
                render = false;
                console.log(e.message + " : " + this.props.id  + " renderedEl:");
                console.log(renderedEL);
            }
        }
        if (render === true) {
            return (
                <a onClick={(event) => this.handleClick(this.props.id, this.props.mnemonicName, event)}
                   title={this.props.tooltip} className={this.props.styleClass}>{this.props.value}</a>
            );
        } else {
            return null;
        }
    }
}

// export default connect(
//     state => ({values: state.process.data, form: state.form})
// )(Link);