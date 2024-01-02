import './style/normalize.css';
import './style/skeleton.css';
import './style/main.scss';
import './style/default.scss';

import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, IndexRoute, hashHistory } from 'react-router';

import { Provider } from 'react-redux'
import { createStore, applyMiddleware, compose } from 'redux'
import thunkMiddleware from 'redux-thunk'

import phiApp from './reducers'
import App from './components/App.jsx';
import Home from './components/Home.jsx';
import Login from './components/Login.jsx';
import PageWrapper from './components/PageWrapper.jsx';
import PageContainer from './components/PageContainer.jsx';
import FlexWrapper from './components/FlexWrapper.jsx';
import DashboardWrapper from './dashboard/DashboardWrapper.jsx';
import {getProcesses} from './actions';

//When Redux devtools is not installed, we’re using Redux compose here.
const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

let store = createStore(
    phiApp,
    composeEnhancers(applyMiddleware(
        thunkMiddleware // lets us dispatch() functions
        //loggerMiddleware // neat middleware that logs actions
    )));

store.dispatch(getProcesses());

//injectTapEventPlugin();

//FIXME use browserHistory

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory}>
            <Route path="/" component={App}>
                <IndexRoute component={Home}/>
                <Route path="/process/:path" component={PageContainer}/>
                <Route path="/flex/:path" component={FlexWrapper}/>
                <Route path="/dashboard/:path" component={DashboardWrapper}/>
            </Route>
            <Route path="/login" component={Login}/>
        </Router>
    </Provider>,
  document.getElementById('app')
);

//persist(alt, storage, 'app');