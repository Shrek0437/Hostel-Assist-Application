import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/Home.jsx";
import AdminClient from "./components/AdminClient.jsx";
import StudentClient from "./components/StudentClient.jsx";

function App() {
  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/admin/*" element={<AdminClient />} />
          <Route path="/student" element={<StudentClient />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
