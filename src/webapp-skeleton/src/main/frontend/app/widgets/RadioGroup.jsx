import React from 'react';
import { connect } from 'react-redux';
import TextBox from './TextBox';

class RadioGroup extends React.Component {
    render() {

        let domain = '';

        if (this.props.values != undefined && this.props.listElementsExpression != null && this.props.listElementsExpression != "") {
            domain = Object.byString(this.props.values, this.props.listElementsExpression);

            if (domain != null && domain instanceof Array) {
                let radioGrp = domain.map(function (d, i) {
                    return (
                        <TextBox key={i} type="radio"  value={d.value} widgetLabel={d.label}/>
                    );
                });

                return (
                    <div>
                        {radioGrp}
                    </div>
                );
            }
        }

        return (
            <TextBox type="radio" {...this.props}/>
        );
    }
}

export default connect(
    state => ({values: state.process.data})
)(RadioGroup);