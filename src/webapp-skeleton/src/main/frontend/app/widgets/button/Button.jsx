import React from 'react';
import { connect } from 'react-redux'
import {manageTask} from '../../actions';

export default class Button extends React.Component {

    //FIXME COPIED FROM LINK!
    handleClick = (id, mnemonicName, event) => {

        //var forms = this.props.form;
        const { form, inject } = this.props;

        function submit(values, dispatch, props) {

            let registeredFields = null; // = form[props.form].registeredFields;

            dispatch(manageTask({id, mnemonicName, inject, registeredFields, values}));
        }

        var executeSubmit = this.props.handleSubmit(submit);
        executeSubmit(event);
    };

    render() {
        return (
            <button type="button" title={this.props.tooltip} className={this.props.styleClass}
                    onClick={(event) => this.handleClick(this.props.id, this.props.mnemonicName, event)}>
                {this.props.children}
            </button>
        );
    }
}

// export default connect(
//     state => ({form: state.form})
// )(Button);