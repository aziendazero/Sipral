import React from 'react';
import { connect } from 'react-redux'
import {startProcess, endProcess} from '../actions';

/**
 * PageWrapper.
 * Contains phi desktop
 * now only PageContainer wich loads jsf pages
 * future: contains jsf or react
 */
class PageWrapper extends React.Component {

    componentWillMount() {
        this.props.dispatch(startProcess(this.props.params.path));
    }

    componentWillReceiveProps (nextProps) {
        if(nextProps.params.path !== this.props.params.path) {
            this.props.dispatch(startProcess(this.props.params.path))
                .then(response => this.forceUpdate());
        }
    }

    componentWillUnmount() {
        this.props.dispatch(endProcess(this.props.params.path));
    }

    getForm(formName) {
        try {
            //return require("../../solution/" + formName);
        } catch (err) {
            console.log("React form not found " + formName); //+ err);
            return null;
        }
    }

    //Same as previous but code splitted, not used for now
    // getFormCS(formName, callback) {
    //     require.ensure([], function(require) {
    //         callback(require("../../generated/" + formName)());
    //     });
    // }

    handleSubmit = (values, dispatch, props) => {
        console.log(values);
        console.log(JSON.stringify(values));
    };

    toggleReact = (values, dispatch, props) => {
        console.log(values);
        console.log(JSON.stringify(values));
    };

    toggleRichFaces = (values, dispatch, props) => {
        console.log(values);
        console.log(JSON.stringify(values));
    };

	render() {
            /*<div style={{height: + this.state.height + 'px'}}>*/
            if (this.props != null && this.props.viewId != null) {
			//if (this.state.page != null) {
				//return (<PageContainer  />);
                var form = this.getForm(this.props.viewId);
                if (form !== null) { //React form present
                    var def = form.default;

                    //const {isChecked} = this.state;

                    return (
                        React.createElement(def, null)
                    );
                } else { //Richfaces form
                    //return (<PageContainer  />);
                    return "React Form " + this.props.viewId + " not present!"
                }
			} else {
                return (
                    'Loading ' +this.props.params.path + ' ...'
                );
            }
           /* </div>*/

	}

}

export default connect(
    state => ({viewId: state.process.viewId})
)(PageWrapper);