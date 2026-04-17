import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function ProtectedRoutes({ children }) {
    const { isAuthenticated, loading } = useAuth();

    if (loading) return <div className="h-screen bg-[#111827]" />;


    if (!isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default ProtectedRoutes;
