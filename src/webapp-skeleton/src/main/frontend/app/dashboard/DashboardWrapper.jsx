/**
 * Created by Alex on 31/03/2017.
 */
import React from 'react';
import { connect } from 'react-redux'
import {dashboardRefresh} from '../actions';
import Adt from '../../solution/MOD_Dashboard/CORE/FORMS/Adt.jsx';
/**
 * Adt Dashboard.
 * Contains flex dashboard
 */
class DashboardWrapper extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentWillMount() {

        let dashboardName = 'adt';

        if (typeof this.props.params !== 'undefined') {
            dashboardName = this.props.params.path;
        }

        this.props.dispatch(dashboardRefresh(dashboardName));

    }

    //

    render() {
        if (this.props.data != null) {
            return (
                <Adt/>
            );
        } else {
            return null;
        }
    }
}

export default connect(
    state => ({data: state.process.data})
)(DashboardWrapper);