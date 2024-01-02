import React from 'react';

export default class DataGridHeader extends React.Component {

    render() {
        return (
            <th>
                {this.props.label}
            </th>
        );
    }
}