import React from 'react';

import FunctionsBean from './actions/FunctionsBean.js';

export default class VirtualPage extends React.Component {

    constructor(props) {
        super(props);
        this.functionsBean = new FunctionsBean();
    }

    render() {

        var pages = this.props.pages;

        return (
            <div>
                {pages(this.functionsBean.empty, this.functionsBean)}
            </div>
        );
    }
}