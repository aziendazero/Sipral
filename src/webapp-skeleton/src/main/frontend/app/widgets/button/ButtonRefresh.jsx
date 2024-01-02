import React from 'react';
import Button from './Button';

export default class ButtonRefresh extends React.Component {
    render() {
        return (
            <Button mnemonicName="REFRESH" {...this.props} styleClass="fa fa-refresh fa-2x"/>
        );
    }
}