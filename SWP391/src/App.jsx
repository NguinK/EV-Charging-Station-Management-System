import "./App.css";
import Homepage from "./components/Homepage";
import LoginPage from "./pages/Login";
import PricePage from "./components/Pricing";
import RegisterPage from "./pages/Register";
import EVHeader from "./components/layout/Header";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AdminR from "./pages/Admin/Dashboard";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Homepage />} />
      <Route path="/price" element={<PricePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/admin" element={<AdminR />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
