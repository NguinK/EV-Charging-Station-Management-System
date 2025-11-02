import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, allowedRoles }) => {
  const userInfo = JSON.parse(localStorage.getItem("userInfo"));
  const token = localStorage.getItem("token");

  if (!token || !userInfo) {
    return <Navigate to="/login" replace />;
  }

  // Nếu vai trò không đúng -> chặn
  if (!allowedRoles.includes(userInfo.role)) {
    // Nếu user cố vào admin
    if (userInfo.role === "USER") return <Navigate to="/user" replace />;
    // Nếu admin cố vào user (tuỳ bạn có cho phép hay không)
    return <Navigate to="/admin" replace />;
  }

  return children;
};

export default ProtectedRoute;
