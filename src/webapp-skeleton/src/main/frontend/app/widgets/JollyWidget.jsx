import React from 'react';

export default class JollyWidget extends React.Component {

    render() {
        let customCode = this.props.customCode || null;
        let output;

        if (customCode != null) {
            try {
                output = customCode();
            } catch (e) {
                console.error(e.message + " : " + this.props.id  + " customCode: " + customCode);
            }
        }
        return (
            <div>
                {this.props.children}
            </div>
        );
    }
}