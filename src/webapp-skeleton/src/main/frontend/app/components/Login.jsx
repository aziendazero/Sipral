import React from 'react';
import { hashHistory } from 'react-router';

import LoginForm from './LoginForm.jsx';

//See:
//https://github.com/mxstbr/login-flow/blob/master/js/components/pages/LoginPage.react.js

export default class Login extends React.Component {
  render() {
    const dispatch = this.props.dispatch;
    //const { formState, currentlySending } = this.props.data;

    var formState = {};
    var currentlySending = false;

    return (
      <div className="login-wrapper">
        <div className="login-form-wrapper">
          <div className="login-header">
            <div className="logo-insiel">
              i
            </div>
            <h2>Login</h2>

          </div>
          {/* While the form is sending, show the loading indicator,
            otherwise show "Log in" on the submit button */
            <LoginForm 
              data={formState} 
              dispatch={dispatch} 
              location={location} 
              history={this.props.history} 
              onSubmit={this._login} 
              btnText={"Login"} 
              currentlySending={currentlySending}/>
          }
        </div>
      </div>
    );
  }

  _login(username, password) {
    //this.props.dispatch(login(username, password));
    hashHistory.push('/');
  }
}