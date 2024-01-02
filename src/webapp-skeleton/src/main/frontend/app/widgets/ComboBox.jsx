import React from 'react';
import { connect } from 'react-redux'
import TextBox from './TextBox';

import {default as DictionaryManager} from '../actions/DictionaryManager'

// const renderSelect = ({ input, label, type, meta: { touched, error } }) => (
//     <select {...input}>
//         <option value="">Select</option>
//         {
//             if (this.props.dictionary != null && this.domainz != null) {
//                 this.props.dictionary[this.domainz].map(this.renderSelectOptions)
//             }
//         }
//     </select>
// );

class ComboBox extends React.Component {

    constructor(props) {
        super(props);

        if (props.domain != undefined) {

            this.domainz = props.domain.replace(":", "_");

                let dictionaryManager = new DictionaryManager();

            this.props.dispatch(dictionaryManager.get(props.domain));
        }
    }

    renderSelect = ({ input, label, type, meta: { touched, error } }) => {
        if (this.props.dictionary != null && this.domainz != null && this.props.dictionary[this.domainz] != null) {
            var options = this.props.dictionary[this.domainz].map((codeValue) => (
                <option key={codeValue.id} value={codeValue.id}>{codeValue.langIt}</option> //FIXME
            ))
        }
        return (
            <select {...input}>
                <option value="">Select</option>
                {options}
            </select>
        )
    };

    renderSelectOptions = (codeValue) => (
        <option key={codeValue.id} value={codeValue.id}>{codeValue.dis}</option>
    );

    render() {
        return (
            <TextBox component={this.renderSelect}  {...this.props}/>
        );
    }
}

export default connect(
    state => ({dictionary: state.dictionary})
)(ComboBox);