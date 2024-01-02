import React from 'react';
import { Link } from 'react-router'
import { connect } from 'react-redux'
import MenuItem from './MenuItem.jsx';
//import {startProcess, endProcess} from '../actions';

//import LeftNav from 'material-ui/lib/left-nav';
//import ListItem from 'material-ui/lib/lists/list-item';


class Menu extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            visible: true
		};
	}

    // componentWillMount() {
    //     this.isVisible();
    // }
    //
    // componentWillUpdate() {
    //     this.isVisible();
    // }


    isVisible = () => {
        if (this.props.isHome && !this.state.visible) {
            this.toggle();
        } else if (!this.props.isHome && this.state.visible) {
            this.toggle();
        }
    };

    toggle = () => {
        this.setState({visible: !this.state.visible});
    };

	render() {
        var self = this;
        //this.isVisible();

        var processes = null;
        if (this.props.processList != null) {
            processes = this.props.processList.map(function (p) {
                return (
                    <li key={p.path}>
                        <Link to={"/process/" + encodeURIComponent(p.path)}>
                            <i className="fa fa-fw fa-hospital-o"></i>{p.title}<span className="fa arrow"></span>
                        </Link>
                        <MenuItem key={p.path} process={p} onProcessClick={self.toggle}/>
                    </li>
                );
            });
        }

        return (
                <ul id="side-menu"
                    className={"menu " + (this.state.visible ? "visible" : "")}>
                    <li key="goHome">
                        <a href="#">
                            <i className="fa fa-fw fa-home"></i>Home<span className="fa arrow"></span>
                        </a>
                    </li>
                    <li key="flexADT">
                        <Link to="/flex/ADT.swf">
                            <i className="fa fa-fw fa-tachometer"></i>Portale di reparto Flex<span className="fa arrow"></span>
                        </Link>
                    </li>
                    <li key="dashboardADT">
                        <Link to="/dashboard/adt">
                            <i className="fa fa-fw fa-tachometer"></i>Portale di reparto<span className="fa arrow"></span>
                        </Link>
                    </li>
                    {processes}
                </ul>
        );
    }
}

export default connect(
    state => ({processList: state.process.processes})/*,
    dispatch => ({onStartProcess: (process) => dispatch(startProcess(process))})*/
    , null, null, { withRef: true }
)(Menu);