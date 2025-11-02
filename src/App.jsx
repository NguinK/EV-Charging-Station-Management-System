import "./App.css";
import Homepage from "./components/Homepage";
import LoginPage from "./pages/Login";
import PricePage from "./components/Pricing";
import RegisterPage from "./pages/Register";
import EVHeader from "./components/layout/Header";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AdminR from "./pages/Admin/AdminPage";
import NotFoundPage from "./components/layout/NotFoundPage";
import BookingPage from "./pages/BookingPage";
import ModalMessage from "./components/ModalMessage";
import HistoryPage from "./pages/Admin/HistoryPage";
import Dashboard from "./pages/Admin/AdminPage";
import Charge from "./pages/User/Charge";
import UserPage from "./pages/User/UserPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Homepage />} />
      <Route path="/price" element={<PricePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/user" element={<UserPage />} />
      <Route path="/admin" element={<AdminR />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default App;

{
  /* <Route
        path="/user"
        element={
          <ProtectedRoute allowedRoles={["USER", "ADMIN"]}>
            <UserPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminR />
          </ProtectedRoute>
        }
      /> */
}
