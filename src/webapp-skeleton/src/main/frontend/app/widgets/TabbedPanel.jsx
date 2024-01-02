import React from 'react';

class TabbedPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tabActive: 1
        };
    }

    handleClick(index, event) {
        this.setState({ tabActive: index });
        event.preventDefault();
    }

    render() {
        var menuItems = this.props.children.map(function (panel, index) {

            var isActive = '';
            if (this.state.tabActive === (index + 1)) {
                isActive = ' is-active'
            }

            return (
                <li ref={'tab-menu-' + (index + 1)} key={index} className={"tabs-menu-item" + isActive}>
                    <a href="#" onClick={(event) => this.handleClick(index + 1, event)}>{panel.props.title}</a>
                </li>
            );
        }.bind(this));

        var panelsList = this.props.children.map(function(panel, index)  {

            var isActive = '';
            if (this.state.tabActive === (index + 1)) {
                isActive = ' is-active'
            }

            return (
                <div ref={'tab-panel-' + (index + 1)} key={index} className={"tabs-panel" + isActive}>
                    {panel}
                </div>
            );
        }.bind(this));

        return (
            <div className="tabset tabbedPanel">
                <nav className="tabs-navigation">
                    <ul className="tabs-menu">
                        {menuItems}
                    </ul>
                </nav>
                <section className="tabs-panels">
                    {panelsList}
                </section>
            </div>
        );
    }
}

class Panel extends React.Component {

    render() {
        return (
            <div>
                {this.props.children}
            </div>
        );
    }
}

export default Object.assign(TabbedPanel, {
    Panel
});