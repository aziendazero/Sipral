import React from 'react';
import TextBox from './TextBox';

export default class TextArea extends React.Component {

    renderTexArea = ({ input, label, type, meta: { touched, error } }) => {
        return (
            <textarea {...input}>
            </textarea>
        )
    };

    render() {
        return (
            <TextBox type="textarea" component={this.renderTexArea} {...this.props}/>
        );
    }
}