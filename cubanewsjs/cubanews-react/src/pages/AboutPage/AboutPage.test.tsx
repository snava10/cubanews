import ReactDOM from "react-dom";
import AboutPage from "./AboutPage";

it("It should mount", () => {
  const div = document.createElement("div");
  ReactDOM.render(<AboutPage />, div);
  ReactDOM.unmountComponentAtNode(div);
});
