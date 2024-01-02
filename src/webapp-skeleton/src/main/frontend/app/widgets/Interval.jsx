import React from 'react';
import MonthCalendar from './MonthCalendar';

export default class Interval extends React.Component {
    render() {
        return (
            <div>
                <MonthCalendar {...this.props}/>
                <MonthCalendar {...this.props} binding={this.props.bindingHigh}/>
            </div>
        );
    }
}