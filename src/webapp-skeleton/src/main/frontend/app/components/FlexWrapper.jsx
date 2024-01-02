import React from 'react';
import { connect } from 'react-redux'
import ReactSWF from 'react-swf'


/**
 * DashboardWrapper.
 * Contains flex dashboard
 */
export default class FlexWrapper extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            flashvars: null
        };
    }

    componentWillMount() {
        console.log("FlexWrapper " + this.props.params.path);

        return fetch("http://localhost:8080/PHI_CI/resource/rest/flexProxy/flashvars", { method: "GET", credentials: 'include'})
            .then(response => response.text())
            .then(response => this.setState({flashvars: response}))
            .catch(error => console.error('Error getting flashvars ' +  error.message, error));
    }
    //
    // componentWillReceiveProps (nextProps) {
    //     //this.resize();
    //     //window.addEventListener('resize', this.resize);
    //     if(nextProps.params.path !== this.props.params.path) {
    //         this.props.dispatch(startProcess(this.props.params.path))
    //             .then(response => this.forceUpdate());
    //     }
    // }
    //
    // componentWillUnmount() {
    //     this.props.dispatch(endProcess(this.props.params.path));
    // }

    //userData=#{flexProxy.getUserData()}&amp;sdlTree=#{flexProxy.getSelectedSdlTree()}&amp;serverTime=#{flexProxy.getServerTime()}&amp;serverGMT=#{flexProxy.getServerGMT()}&amp;language=#{localeSelector.language}&amp;patientId=#{Patient.internalId}&amp;patientEncounterId=#{PatientEncounter.internalId}&amp;assignedSdlId=#{PatientEncounter.assignedSDL.internalId}&amp;temporarySDLId=#{PatientEncounter.temporarySDL.internalId}&amp;therapyId=#{PatientEncounter.therapy[0].internalId}&amp;conversationId=#{conversation.id}&amp;customer=#{CUSTOMER}&amp;solution=PHI_CI&amp;modulesToOpen=#{modulesToOpen}&amp;lastModifiedTimes=#{cacheManager.getModulesLastModifiedTimes("PHI_CI")}&amp;filterForPrivacy=#{repository.getSeamProperty("filterForPrivacy")}'

	render() {
        if (this.state.flashvars != null) {
            return (
                // <embed src="http://localhost:8080/PHI_CI/swf/MDashboard.swf"
                //        width="100%"
                //        height="100%"
                //        align="middle"
                //        id="MDashboard"
                //        name="MDashboard"
                //        wmode="opaque"
                //        flashvars={this.state.flashvars}
                //        type="application/x-shockwave-flash"/>
                <div id="flex-wrapper" className="container">
                <ReactSWF
                    src="http://localhost:8080/PHI_CI/swf/MDashboard.swf"
                    width="100%"
                    height="100%"
                    id="MDashboard"
                    wmode="gpu"
                    flashVars={this.state.flashvars}
                />
                </div>
            );
        } else {
            return null;
        }
	}
}