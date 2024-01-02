import React from 'react';

export default class Form extends React.Component {

    render() {
        //const { handleSubmit, pristine, reset, submitting } = props;
        //<form id={this.props.id} onSubmit={this.props.onSubmit}>
        return (
            <form id={this.props.id}>
                <h1 id="formTitle">{this.props.label}</h1>
                {this.props.children}
            </form>
        );
    }
}