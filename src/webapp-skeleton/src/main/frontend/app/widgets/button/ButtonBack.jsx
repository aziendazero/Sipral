import React from 'react';
import Button from './Button';

export default class ButtonBack extends React.Component {
    render() {
        return (
            <Button mnemonicName="BACK" {...this.props} styleClass="fa fa-arrow-left fa-3x"/>
        );
    }
}