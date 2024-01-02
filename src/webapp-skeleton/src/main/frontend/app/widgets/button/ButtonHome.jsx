import React from 'react';
import { hashHistory } from 'react-router';
//import PhiActions from '../../actions/PhiActions';
//import {startProcess, endProcess} from '../../actions';


export default class ButtonHome extends React.Component {

    goHome = () => {
        //this.props.dispatch(endProcess(process.path));
        //console.log(this.props.router); //FIXME USE THIS
        hashHistory.push('/');
    };

    render() {
        var self = this;
        return (
            <button type="button" title={this.props.tooltip} className="fa fa-home fa-3x" onClick={() => this.goHome()}>{this.props.value}</button>
        );
    }
}