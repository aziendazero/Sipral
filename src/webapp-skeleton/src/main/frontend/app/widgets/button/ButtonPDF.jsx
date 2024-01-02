import React from 'react';
import Button from './Button';

export default class ButtonPDF extends React.Component {
    render() {
        return (
            <Button mnemonicName="PDF" {...this.props} styleClass="fa fa-file-pdf-o fa-3x"/>
        );
    }
}