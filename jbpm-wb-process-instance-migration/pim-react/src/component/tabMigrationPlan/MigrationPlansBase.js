import React from "react";
import axios from "axios";

import {
  MockupData_planList,
  MockupData_runningInstances,
  MockupData_PIM_response
} from "../common/MockupData";
import { BACKEND_URL, USE_MOCK_DATA } from "../common/PimConstants";

export default class MigrationPlansBase extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      plans: [],
      filteredPlans: [],
      showDeleteConfirmation: false,
      showMigrationWizard: false,
      showPlanWizard: false,
      deletePlanId: "",
      runningInstances: [],
      addPlanResponseJsonStr: ""
    };
  }

  componentDidMount() {
    this.retrieveAllPlans();
  }

  retrieveAllPlans = () => {
    if (USE_MOCK_DATA) {
      //console.log("retrieveAllPlans useing mock data: ");
      const plans = MockupData_planList;
      this.setState({ plans, filteredPlans: plans });
    } else {
      const servicesUrl = BACKEND_URL + "/plans";
      //console.log("retrieveAllPlans servicesUrl: " + servicesUrl);
      axios.get(servicesUrl, {}).then(res => {
        const plans = res.data;
        //console.log('retrieveAllPlans ' + JSON.stringify(plans));
        this.setState({ plans, filteredPlans: plans });
        //console.log("retrieveAllPlans is done ");
      });
    }
  };

  showDeleteDialog = id => {
    this.setState({
      showDeleteConfirmation: true,
      deletePlanId: id
    });
    //console.log("deletePlanId " + id);
  };

  hideDeleteDialog = () => {
    this.setState({
      showDeleteConfirmation: false
    });
  };

  deletePlan = () => {
    if (USE_MOCK_DATA) {
      this.retrieveAllPlans();
      this.hideDeleteDialog();
    } else {
      //need to create a temp variable "self" to store this, so I can invoke this inside axios call
      const self = this;
      const serviceUrl = BACKEND_URL + "/plans/" + this.state.deletePlanId;
      //console.log("delete url: " + serviceUrl);

      axios
        .delete(serviceUrl, {
          headers: {
            "Content-Type": "application/json"
          }
        })
        .then(function() {
          //console.log("delete response: " + response.data);
          self.retrieveAllPlans();
          self.hideDeleteDialog();
        });
    }
  };

  // addPlan need to be in the parent because it's shared between WizardAddPlan and Import Plan pop-up
  addPlan = plan => {
    if (USE_MOCK_DATA) {
      this.setState({
        addPlanResponseJsonStr: JSON.stringify(MockupData_PIM_response, null, 2)
      });
      this.retrieveAllPlans();
    } else {
      //console.log("addPlan is invoked");
      if (plan !== null && plan !== "") {
        //console.log('submit plan' + plan);

        //step 1, replace all \" to "
        plan = plan.replace(/\\"/g, '"');
        //console.log('submit plan1: ' + plan);
        //step 2, replace "{ to {
        plan = plan.replace('"{', "{");
        //console.log("plan2: " + plan);
        //step3, replace }" to }
        plan = plan.replace('}"', "}");
      }

      //need to create a temp variable "self" to store this, so I can invoke this inside axios call
      const self = this;
      const servicsUrl = BACKEND_URL + "/plans";
      //console.log("servicsUrl: " + servicsUrl);
      axios
        .post(servicsUrl, plan, {
          headers: {
            "Content-Type": "application/json"
            //"Content-Type": "text/plain"
            //"Content-Type": "application/x-www-form-urlencoded"
            //"Content-Type": "multipart/form-data"
          }
        })
        .then(function(response) {
          //console.log("addPlan response: " + response.data);
          self.setState({
            addPlanResponseJsonStr: JSON.stringify(response.data, null, 2)
          });
          self.retrieveAllPlans();
        });
    }
  };

  editPlan = (plan, planId) => {
    if (USE_MOCK_DATA) {
      this.retrieveAllPlans();
    } else {
      //need to create a temp variable "self" to store this, so I can invoke this inside axios call
      const self = this;
      const serviceUrl = BACKEND_URL + "/plans/" + planId;
      //console.log("serviceUrl: " + serviceUrl);
      //console.log("edit plan: " + plan);
      axios
        .put(serviceUrl, plan, {
          headers: {
            "Content-Type": "application/json"
          }
        })
        .then(function(response) {
          //console.log("editPlan response: " + response.data);
          self.setState({
            addPlanResponseJsonStr: JSON.stringify(response.data, null, 2)
          });
          self.retrieveAllPlans();
        });
    }
  };

  openMigrationWizard = rowData => {
    if (USE_MOCK_DATA) {
      const instances = MockupData_runningInstances;
      //console.log('running instances: ' + JSON.stringify(instances));

      this.setState({
        runningInstances: instances,
        showMigrationWizard: true,
        planId: rowData.id
      });
      this.refs.WizardExecuteMigrationChild.resetWizardStates();
    } else {
      const servicsUrl = BACKEND_URL + "/instances";
      axios
        .get(servicsUrl, {
          params: {
            containerId: rowData.sourceContainerId,
            kieserverId: this.props.kieServerIds
          }
        })
        .then(res => {
          const instances = res.data;
          //console.log('running instances: ' + JSON.stringify(instances));

          this.setState({
            runningInstances: instances,
            showMigrationWizard: true,
            planId: rowData.id
          });
          this.refs.WizardExecuteMigrationChild.resetWizardStates();
        });
    }
  };
}
