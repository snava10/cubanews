import "./App.css";
import { Link, BrowserRouter, Routes, Route } from "react-router-dom";
import { Search } from "./Pages/Search";
import { About } from "./Pages/About";
import ReactGA from "react-ga4";

const Home = () => {
  return (
    <div className="App">
      <header className="App-header">
      <Link to="/search">Search</Link>
      </header>
    </div>
  );
};

function App() {
  if (process.env.REACT_APP_ENV === 'PROD') {
    ReactGA.initialize('G-NLVF1S815F');
  } 
  
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Search/>} />
        <Route path="/about" element={<About/>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
