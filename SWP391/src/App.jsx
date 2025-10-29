import "./App.css";
import HomePage from "./components/HomePage";
import LoginPage from "./pages/Login";
import PricePage from "./components/Pricing";
import RegisterPage from "./pages/Register";
import { Routes, Route } from "react-router-dom";
import AdminR from "./pages/Admin/Dashboard";
import NotFoundPage from "./pages/NotFoundPage";
import BookingPage from "./pages/BookingPage";
import ModalMessage from "./pages/ModalMessage";
import HistoryPage from "./pages/Admin/HistoryPage";
import Dashboard from "./pages/Admin/Dashboard";

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/price" element={<PricePage />} />
      <Route path="/booking" element={<BookingPage />} />
      <Route path="/history" element={<HistoryPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/admin" element={<AdminR />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/modal" element={<ModalMessage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default App;
