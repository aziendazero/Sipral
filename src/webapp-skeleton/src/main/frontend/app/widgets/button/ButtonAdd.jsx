import React from 'react';
import Button from './Button';

export default class ButtonAdd extends React.Component {
    render() {
        return (
            <Button mnemonicName="ADD" {...this.props} styleClass="fa fa-plus fa-3x"/>
        );
    }
}