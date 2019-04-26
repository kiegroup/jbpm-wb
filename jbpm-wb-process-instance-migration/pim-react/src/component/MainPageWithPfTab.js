import React, { Component } from "react";
import classNames from "classnames";
import axios from "axios";

import {
  TabContainer,
  Nav,
  NavItem,
  TabPane,
  TabContent
} from "patternfly-react";
import { DropdownButton, MenuItem } from "patternfly-react";

import MigrationPlans from "./tabMigrationPlan/MigrationPlans";
import MigrationDefinitions from "./tabMigration/MigrationDefinitions";
import { MockupData_KIE_SERVER_ID } from "./common/MockupData";
import { USE_MOCK_DATA } from "./common/PimConstants";
import { BACKEND_URL } from "./common/PimConstants";

export default class MainPageWithPfTab extends Component {
  constructor(props) {
    super(props);

    this.state = {
      kieServerIds: "",
      title: "KIE Server Name",
      menuItems: []
    };
  }

  componentDidMount() {
    let kieServerIds;
    let menuItems = this.state.menuItems;
    if (USE_MOCK_DATA) {
      kieServerIds = MockupData_KIE_SERVER_ID;
      const kieServerIdsArray = kieServerIds.split(",");
      kieServerIdsArray.map((id, i) => {
        if (i == 0) {
          const newTitle = "KIE Server Name:" + id;
          this.setState({ title: newTitle });
          this.setState({ kieServerIds: id });
        }
        menuItems.push(
          <MenuItem key={i} eventKey={id} onSelect={this.handleChange}>
            {id}
          </MenuItem>
        );
      });
      this.setState({ menuItems });
    } else {
      const servicesUrl = BACKEND_URL + "/kieserverids";
      axios.get(servicesUrl, {}).then(res => {
        kieServerIds = res.data;
        const kieServerIdsArray = kieServerIds.split(",");
        kieServerIdsArray.map((id, i) => {
          if (i == 0) {
            const newTitle = "KIE Server Name:" + id;
            this.setState({ title: newTitle });
            this.setState({ kieServerIds: id });
          }
          menuItems.push(
            <MenuItem key={i} eventKey={id} onSelect={this.handleChange}>
              {id}
            </MenuItem>
          );
        });
        this.setState({ menuItems });
      });
    }
  }

  handleChange = option => {
    const newTitle = "KIE Server Name:" + option;
    this.setState({ title: newTitle });
    this.setState({ kieServerIds: option });
  };

  render() {
    const bsClass = classNames("nav nav-tabs nav-tabs-pf", {
      "nav-justified": false
    });

    return (
      <div className="">
        <div className="row">
          <div className="col-xs-9">
            <h1>Process Instance Migration</h1>
          </div>
          <div className="col-xs-3">
            <br />
            <div className="pull-right">
              <DropdownButton id={"kieDropDown"} title={this.state.title}>
                {this.state.menuItems}
              </DropdownButton>
            </div>
          </div>
        </div>
        <TabContainer id="tabs-with-dropdown-pf" defaultActiveKey="first">
          <div>
            <Nav bsClass={bsClass}>
              <NavItem eventKey="first">Migration Plans</NavItem>
              <NavItem eventKey="second">Migrations</NavItem>
            </Nav>

            <TabContent animation>
              <TabPane eventKey="first">
                <MigrationPlans kieServerIds={this.state.kieServerIds} />
              </TabPane>
              <TabPane eventKey="second">
                <MigrationDefinitions />
              </TabPane>
            </TabContent>
          </div>
        </TabContainer>
      </div>
    );
  }
}
