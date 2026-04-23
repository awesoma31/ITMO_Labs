// src/components/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { auth } from "../auth";

export default function ProtectedRoute({ children }) {
  // Проверяем токен прямо сейчас, в момент рендера маршрута
  const token = auth.getToken(); 
  
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
