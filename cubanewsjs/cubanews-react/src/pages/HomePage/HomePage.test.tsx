import ReactDOM from "react-dom";
import HomePage from "./HomePage";

it("It should mount", () => {
  const div = document.createElement("div");
  ReactDOM.render(<HomePage />, div);
  ReactDOM.unmountComponentAtNode(div);
});
