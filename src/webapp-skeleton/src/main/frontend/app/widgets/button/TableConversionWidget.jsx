import React from 'react';
import Button from './Button';

export default class TableConversionWidget extends React.Component {
    render() {
        return (
            <Button {...this.props} styleClass="fa fa-file-excel-o fa-3x"/>
        );
    }
}