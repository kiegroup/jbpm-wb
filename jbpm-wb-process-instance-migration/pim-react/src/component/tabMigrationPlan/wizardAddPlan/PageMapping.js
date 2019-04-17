import React, { Component } from "react";
import { Button } from "patternfly-react";

import PageMappingDiagrams from "./PageMappingDiagrams";
import PageMappingDropdownNode from "./PageMappingDropdownNode";

export default class PageMapping extends Component {
  //need to provide some dummy data for the selectors which are used in svg pan, because
  //it's required and can't be empty, and need to start with "_"
  //can't do the initial in the sub components like PageMappingDiagramsSvgPan because they
  //are passed in through props
  constructor(props) {
    super(props);
    this.state = {
      sourceNodeStr: "",
      targetNodeStr: "",
      sourceDiagramshown: false,
      targetDiagramshown: false,
      sourceCurrentSelector: "_Dummy123",
      sourcePreviousSelector: "_Dummy123",
      targetCurrentSelector: "_Dummy123",
      targetPreviousSelector: "_Dummy123"
    };
  }

  //This is used in add plan wizard (for edit plan) to load the inital data to form fields
  //  componentDidMount(){
  //      var mappingField =  document.getElementById("nodeMappingField");
  //      console.log('PageMapping componentDidMount mappingField ' + mappingField);
  //     mappingField.value=JSON.stringify(this.props.mappings);
  //  }
  componentDidUpdate() {
    var mappingField = document.getElementById("nodeMappingField");
    if (mappingField != null) {
      if (this.props.mappings !== null && this.props.mappings != "") {
        if (mappingField.value === null || mappingField.value === "") {
          mappingField.value = JSON.stringify(this.props.mappings);
        }
      }
    }
  }

  handleSourceDiagramButtonClick = () => {
    //console.log(" sourceDiagramshown " + this.state.sourceDiagramshown);
    this.setState({
      sourceDiagramshown: !this.state.sourceDiagramshown
    });
  };

  handleTargetDiagramButtonClick = () => {
    //console.log("targetDiagramshown " + this.state.targetDiagramshown);
    this.setState({
      targetDiagramshown: !this.state.targetDiagramshown
    });
  };

  handleSourceDropdownChange = option => {
    let tmpPreviousSelector = this.state.sourceCurrentSelector;
    //let tmpCurrentSelector = "#"  + option.value + "undefined";
    let tmpCurrentSelector = "#" + option + "_shapeType_BACKGROUND";
    this.setState({
      sourceNodeStr: option,
      sourcePreviousSelector: tmpPreviousSelector,
      sourceCurrentSelector: tmpCurrentSelector,
      sourceDiagramshown: true,
      targetDiagramshown: false
    });
  };

  handleTargetDropdownChange = option => {
    let tmpPreviousSelector = this.state.targetCurrentSelector;
    let tmpCurrentSelector = "#" + option + "_shapeType_BACKGROUND";

    this.setState({
      targetNodeStr: option,
      targetPreviousSelector: tmpPreviousSelector,
      targetCurrentSelector: tmpCurrentSelector,
      sourceDiagramshown: false,
      targetDiagramshown: true
    });
  };

  handleMapButtonClick = () => {
    if (
      this.state.sourceNodeStr.length > 0 &&
      this.state.targetNodeStr.length > 0
    ) {
      var currentNodeMapping =
        '"' +
        this.state.sourceNodeStr +
        '"' +
        ":" +
        '"' +
        this.state.targetNodeStr +
        '"';
      //console.log("currentNodeMapping1 ", currentNodeMapping);

      var input = document.getElementById("nodeMappingField");
      var currentInputValue = input.value;
      //remove {} before add new node mapping values
      currentInputValue = currentInputValue.replace(/{/g, "");
      currentInputValue = currentInputValue.replace(/}/g, "");
      if (currentInputValue.length > 0) {
        currentInputValue = currentInputValue + "," + currentNodeMapping;
      } else {
        currentInputValue = currentNodeMapping;
      }

      currentInputValue = "{" + currentInputValue + "}";

      var nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLTextAreaElement.prototype,
        "value"
      ).set;
      nativeInputValueSetter.call(input, currentInputValue);

      //once fired the event, this currentInputValue will be saved in the wizard form's values
      var ev2 = new Event("input", { bubbles: true });
      input.dispatchEvent(ev2);
    }
  };

  MappingButton() {
    return (
      <Button bsStyle="primary" onClick={this.handleMapButtonClick}>
        Map these two nodes
      </Button>
    );
  }

  render() {
    const sourceValues = this.props.sourceInfo.values;
    const sourceLabels = this.props.sourceInfo.labels;
    const sourceNode = [];
    if (this.props.sourceInfo !== null && this.props.sourceInfo !== "") {
      for (var i = 0; i < sourceValues.length; i++) {
        sourceNode.push({ value: sourceValues[i], label: sourceLabels[i] });
      }

      //const sourceNode = [{value:'_D3E17247-1D94-47D8-93AD-D645E317B736',label:'Self Evaluation:_D3E17247-1D94-47D8-93AD-D645E317B736'},{value:'_E35438DF-03AF-4D7B-9DCB-30BC70E7E92E',label:'PM Evaluation:_E35438DF-03AF-4D7B-9DCB-30BC70E7E92E'},{value:'_AB431E82-86BC-460F-9D8B-7A7617565B36',label:'HR Evaluation:_AB431E82-86BC-460F-9D8B-7A7617565B36'},{value:'_B8E4DA1E-A62B-49C2-9A94-FEE5F5FD2B4E',label:'Input:_B8E4DA1E-A62B-49C2-9A94-FEE5F5FD2B4E'}];
      //const targetNode = [{value:'_D3E17247-1D94-47D8-93AD-D645E317B736',label:'Self Evaluation:_D3E17247-1D94-47D8-93AD-D645E317B736'},{value:'_E35438DF-03AF-4D7B-9DCB-30BC70E7E92E',label:'PM Evaluation:_E35438DF-03AF-4D7B-9DCB-30BC70E7E92E'},{value:'_AB431E82-86BC-460F-9D8B-7A7617565B36',label:'HR Evaluation:_AB431E82-86BC-460F-9D8B-7A7617565B36'},{value:'_B8E4DA1E-A62B-49C2-9A94-FEE5F5FD2B4E',label:'Input:_B8E4DA1E-A62B-49C2-9A94-FEE5F5FD2B4E'}];
      const targetValues = this.props.targetInfo.values;
      const targetLabels = this.props.targetInfo.labels;
      const targetNode = [];
      for (var j = 0; j < targetValues.length; j++) {
        targetNode.push({ value: targetValues[j], label: targetLabels[j] });
      }

      return (
        <div className="form-horizontal">
          <div className="form-group">
            <label>Source: {this.props.sourceInfo.processId}</label>
            <PageMappingDropdownNode
              options={sourceNode}
              title="Source Nodes "
              onDropdownChange={this.handleSourceDropdownChange}
            />
            <br />
            <label>Target: {this.props.targetInfo.processId}</label>
            <PageMappingDropdownNode
              options={targetNode}
              title="Target Nodes "
              onDropdownChange={this.handleTargetDropdownChange}
            />
            <br />
            {this.MappingButton()}
          </div>

          <div className="form-group">
            <label>
              Use the text field below to update mappings directly (e.g. to
              delete an incorrect mapping)
            </label>

            <textarea
              className="form-control"
              name="mappings"
              id="nodeMappingField"
              rows="2"
            />
          </div>

          <PageMappingDiagrams
            sourceCurrentSelector={this.state.sourceCurrentSelector}
            sourcePreviousSelector={this.state.sourcePreviousSelector}
            targetCurrentSelector={this.state.targetCurrentSelector}
            targetPreviousSelector={this.state.targetPreviousSelector}
            sourceDiagramButtonClick={this.handleSourceDiagramButtonClick}
            targetDiagramButtonClick={this.handleTargetDiagramButtonClick}
            sourceDiagramshown={this.state.sourceDiagramshown}
            targetDiagramshown={this.state.targetDiagramshown}
            sourceInfo={this.props.sourceInfo}
            targetInfo={this.props.targetInfo}
          />
        </div>
      );
    } else {
      //no process info retrived from backend yet, just display an empty tag to avoid error.
      return <div />;
    }
  }
}
