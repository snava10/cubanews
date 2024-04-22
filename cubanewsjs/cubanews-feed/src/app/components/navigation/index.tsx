"use client";
import { useState } from "react";
import Navbar from "./navbar";

const Navigation = () => {
  // toggle sidebar
  const [isOpen, setIsOpen] = useState(false);
  const toggle = () => {
    setIsOpen(!isOpen);
  };
  return (
    <>
      <Navbar />
    </>
  );
};

export default Navigation;
