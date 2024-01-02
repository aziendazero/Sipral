import React from 'react';
import { browserHistory } from 'react-router';
import { connect } from 'react-redux'
import {getForm} from '../actions';
import { A4J } from '../legacy/jsfajax-minimal.js';
import Phi from '../legacy/js.js';
import Tree from '../legacy/tree.js';

/**
 * PageContainer.
 * Load a page and show it
 */
class PageContainer extends React.Component {

	constructor(props) { 
    	super(props);

        this.notCalled = true;

		//hack to see Phi from jsf pages
        if (!window.Phi) {
        	this.phi = new Phi();
            window['goHome'] = this.goHome.bind(this);
            window['focusFirstElement'] = this.phi.focusFirstElement.bind(this.phi);
            window['preventNavigation'] = this.phi.preventNavigation.bind(this.phi);
            window['manageTables'] = this.phi.manageTables.bind(this.phi);
            window['openFormPopup'] = this.phi.openFormPopup.bind(this.phi);
            window['showDateTimePicker'] = this.phi.showDateTimePicker.bind(this.phi);
            window['filterTbl'] = this.phi.filterTbl.bind(this.phi);

        	var tree = new Tree();
        	window.Tree = tree;
        }

        this.props.dispatch(getForm())
            .then(response => {
                if (response != null) {
                    this.viewState = response.viewState;
                    this.phi.lang = response.lang;
                }
            });

		this.state = {
		};
	}

    componentWillReceiveProps (nextProps) {
        this.path = nextProps.params.path;
    }

    shouldComponentUpdate(nextProps, nextState) {
        if (nextProps.viewState && this.notCalled) {
            this.notCalled = false;
            return true;
        }
        return false;
    }
	
	render() {
        if (this.props.viewState != null) {
            return (
            <div>
                <form id="hf" name="hf" method="post"  target="">
                    <input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value={this.props.viewState} autoComplete="off" />
                    <input type="hidden" autoComplete="off" name="hf" value="hf"/>
                    <input type="hidden" autoComplete="off" name="autoScroll" value=""/>
                </form>
            <div id="phiDesktop">
                <form id="f" name="f" method="post" target="">
                </form>
            </div>
            <div id="popup">
                <form id="fPop" name="f" method="post" target="">
                </form>
            </div>
            <span id="_viewRoot:status" style={{display: 'none'}}>
              <span id="_viewRoot:status.start"/>
              <span id="_viewRoot:status.stop"/>
            </span>
            </div>
            );
        } else {
            return null;
        }

	}

    componentDidUpdate() {

        document.getElementById('_viewRoot:status.start')['onstart'] = this.startAjaxReq.bind(this);

        document.getElementById('_viewRoot:status.stop')['onstop'] = this.stopAjaxReq.bind(this);

        A4J.AJAX.Submit('hf', null,
            {
                'similarityGroupingId': 'hf:startProcess',
                'parameters': {
                    'ajaxSingle': 'hf:startProcess',
                    'hf:startProcess': 'hf:startProcess',
                    'currentProcess': this.path
                },
                'actionUrl': this.baseUrl // '/PHI_CI/home-jsf.seam'
            });
    }

    startAjaxReq() {
        this.phi.startAjaxReq();
    }

    stopAjaxReq() {
        this.phi.stopAjaxReq();
    }

    goHome() {
        browserHistory.push('/');
    }

}

export default connect(
	state => ({viewState: state.formJsf.viewState})
)(PageContainer);