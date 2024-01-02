import React from 'react';
import TextBox from './TextBox';

export default class CheckBox extends React.Component {
    render() {
        return (
            <TextBox type="checkbox" {...this.props}>{this.props.children}</TextBox>
        );
    }
}