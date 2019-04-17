import React from "react";
import ReactDOM from "react-dom";
import MainPagewithPfTab from "../component/MainPagewithPfTab";

describe("Main Page", () => {
  it("renders without crashing", () => {
    const div = document.createElement("div");
    ReactDOM.render(<MainPagewithPfTab />, div);
  });
});
