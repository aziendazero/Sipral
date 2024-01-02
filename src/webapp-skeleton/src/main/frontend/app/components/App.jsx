import React from 'react';
import { connect } from 'react-redux'
import HeaderBar from './HeaderBar.jsx';
import Menu from './Menu.jsx';
import Loader from './Loader.jsx';

export default class App extends React.Component {

    render() {

        let isHome = this.props.location.pathname == '/';

        return (
            <div id="wrapper">
                <HeaderBar handleMenuToggle={() => this.refs.menu.getWrappedInstance().toggle()}/>
                <Menu isHome={isHome} ref="menu"/>
                <div id="page-wrapper" className="container menu-visible">
                    {this.props.children}
                </div>
                <Loader/>
            </div>
        );
    }
}
