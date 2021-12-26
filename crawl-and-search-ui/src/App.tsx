import React from "react";
import logo from "./logo.svg";
import "./App.css";
import ReactDOM from "react-dom";
import { Link, BrowserRouter, Routes, Route } from "react-router-dom";
import { Search } from "./Pages/Search";

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
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/search" element={<Search />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
