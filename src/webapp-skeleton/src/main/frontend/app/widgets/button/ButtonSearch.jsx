import React from 'react';
import Button from './Button';

export default class ButtonSearch extends React.Component {
    render() {
        return (
            <Button mnemonicName="SEARCH" {...this.props} styleClass="fa fa-search fa-2x"/>
        );
    }
}