import React from 'react';
import TextBox from './TextBox';

export default class MonthCalendar extends React.Component {

    formatDate(value) {
        if (value == null) {
            value = '';
        }
        if (value instanceof Date) {
            value = value.toDateString();
        }
        return value;
    }

    render() {
        return (
            <TextBox type="date" {...this.props} format={this.formatDate}/>
        );
    }
}