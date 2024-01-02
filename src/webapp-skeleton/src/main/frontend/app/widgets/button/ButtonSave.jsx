import React from 'react';
import Button from './Button';

export default class ButtonSave extends React.Component {
    render() {
        return (
            <Button mnemonicName="SAVE" {...this.props} styleClass="fa fa-floppy-o fa-3x"/>
        );
    }
}