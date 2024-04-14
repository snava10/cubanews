import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ReactGA from "react-ga4";
import HomePage from "./pages/HomePage/HomePage";
import AboutPage from "./pages/AboutPage/AboutPage";

function App() {
  if (process.env.REACT_APP_ENV === "PROD") {
    ReactGA.initialize("G-NLVF1S815F");
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<AboutPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
