import React from 'react';
import { Link } from 'react-router'

//import ListItem from 'material-ui/lib/lists/list-item';

export default class MenuItem extends React.Component {

  render() {
    var self = this;

    if (this.props.process == null  || this.props.process.children == null) {
      return null;
    }

    // var nestedItems = [];
    // if (this.props.process.children != null) {
    //   this.props.process.children.map(function(pchild) {
    //     nestedItems.push(<MenuItem key={pchild.path} process={pchild} onProcessClick={self.props.onProcessClick}  />);
    //   })
    // }
    //
    // return (
    //   <ListItem
    //     primaryText={this.props.process.title}
    //     primaryTogglesNestedList={!this.props.process.leaf}
    //     nestedItems={nestedItems}
    //     nestedListStyle={{marginLeft: 36 + 'px'}}
    //     onTouchTap={() => this.props.onProcessClick(this.props.process)} />
    // );

    var childClass = "nav nav-second-level";
    if (this.props.process.collapsed == true) {
      childClass += " collapse in";
    } else {
      childClass += " collapse";
    }

    return (
        <ul className={childClass}>
          {this.props.process.children.map(function(pchild) {
            return (
                <li key={pchild.path}>
                    <Link to={"/process/" + encodeURIComponent(pchild.path)} onClick={() => self.props.onProcessClick(pchild)}>{pchild.title}</Link>
                    <ul className="nav nav-third-level">
                        <MenuItem key={pchild.path} process={pchild} onProcessClick={self.props.onProcessClick}/>
                    </ul>
                </li>
            );
          })}
        </ul>
    );
  }
}