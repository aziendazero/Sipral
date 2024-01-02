import React from 'react';
import { connect } from 'react-redux'

class Loader extends React.Component {

    render() {
        // if (this.props.loading) {
            return (
                <div id="loader" className="overlay-loading zIndexFront" style={{display: this.props.loading ? 'table' : 'none'}}>
                    <div className="centered">
                        <i className="fa fa-refresh fa-spin fa-4x zIndexFront"></i>
                    </div>
                </div>
            );
        // } else {
        //     return null;
        // }
    }
}

export default connect(
    state => ({loading: state.process.loading})
)(Loader);