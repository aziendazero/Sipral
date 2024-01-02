import React from 'react';

export default class DataGridColumn extends React.Component {

    render() {
        return (
            <td>
                {this.props.children}
            </td>
        );
    }
}