import React from 'react';
import { connect } from 'react-redux'
import { hashHistory } from 'react-router';

class HeaderBar extends React.Component {

  handleLogOut(e) {
    hashHistory.push('/login');
  }


  render() {
    var title = 'Spisal';
    if (this.currentProcess != null) {
      title = this.currentProcess.path;
    }
      //FIXME custom header for SPISAL, move to solution
      let banner = null;
      if (this.props.values != null) {
          let Procpratiche = this.props.values.Procpratiche;
          if (Procpratiche != null) {
              banner =
                  <section className="container">
                      <div className="one-half column">
                          <label>Numero pratica: {Procpratiche.numero}</label>
                      </div>
                  </section>
          }
          }
    return (
        <header>
            <nav className="container">
            <div className="one-half column">
            <ul>
                <li>
                    <a href="#">
                        <i className="fa fa-fw fa-home"></i>Home<span className="fa arrow"></span>
                    </a>
                </li>
                <li>
                    <a id="toggle-nav" onClick={this.props.handleMenuToggle}>
                        <i className="fa fa-bars"/>Menu
                    </a>
                </li>
            </ul>
            </div>
                <div className="one-half column">
            <ul>
                <li>
                    <a id="refresh"><i className="fa fa-refresh"/>Refresh</a>
                </li>
                <li>
                    <a id="help"><i className="fa fa-question"/>Help</a>
                </li>
                <li>
                    <a id="logout" onClick={this.handleLogOut.bind(this)}><i className="fa fa-sign-out"/>Logout</a>
                </li>
            </ul>
            </div>
            </nav>
            {banner}
        </header>
    );
  }
}


export default connect(
    state => ({values: state.process.data}) //FIXME custom header for SPISAL, move to solution
)(HeaderBar);