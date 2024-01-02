import React from 'react';

export default class DataGrid extends React.Component {

    render() {
        return (
            <div id={this.props.id} className="dt">
                <table>
                    {this.props.children}
                </table>
            </div>
        );
    }
}