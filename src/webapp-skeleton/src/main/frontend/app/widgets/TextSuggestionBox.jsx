import React from 'react';
import TextBox from './TextBox';

export default class TextSuggestionBox extends React.Component {
    render() {
        return (
            <TextBox type="text"  {...this.props}/>
        );
    }
}