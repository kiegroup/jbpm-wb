import React from "react";
import ReactDOM from "react-dom";

import MainPagewithPfTab from "./component/MainPagewithPfTab";

document.addEventListener("DOMContentLoaded", function() {
  ReactDOM.render(
    React.createElement(MainPagewithPfTab),
    document.getElementById("mount")
  );
});
