import React from 'react';

export default class Layout extends React.Component {

    render() {
        const orientation = this.props.orientation || 'horizontal';
        const alignment = this.props.alignment || '';

        var clazz = '';

        if (typeof this.props.styleClass !== 'undefined') {
            clazz = this.props.styleClass + ' '
        }

        if (orientation == 'vertical') {
            clazz += 'layout vertical';
            //clazz = 'column';
        } else {
            clazz += 'layout horizontal';
            //clazz = 'row';
        }

        clazz += ' ' + alignment;

        var render = this.props.render || true;
        var renderedEL = this.props.renderedEL; //|| true;
        if (render == 'no') {
            render = false;
        }
        if (renderedEL != undefined) {
            //let functions = FunctionsBean;
            //let empty = isEmpty;
            try {
                render = renderedEL();
            } catch (e) {
                //render = false;
                console.log(e.message + " : " + this.props.id  + " renderedEl:");
                console.log(renderedEL);
            }
        }
        if (render) {

            if (this.props.asGroupBox) {
                return (
                    <fieldset id={this.props.id} className={'layoutGroupBox ' + clazz + ' ' + this.props.styleClass} style={this.props.style}>
                        <legend className="groupBoxLegend">{this.props.label}</legend>
                        {this.props.children}
                    </fieldset>
                );
            } else {
                return (
                    <fieldset id={this.props.id} className={clazz} style={this.props.style}>
                        {this.props.children}
                    </fieldset>
                );
            }

        } else {
            return null;
        }
    }
}