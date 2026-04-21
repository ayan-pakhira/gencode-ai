import { createContext, useContext, useEffect, useState } from "react";
import {logoutUser} from "../services/PublicApi.js";
import toast from 'react-hot-toast';
import { setToken, getToken, clearToken } from "../services/TokenService.js";

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const storedToken = getToken();
        if (storedToken) {
            setToken(storedToken);
            setIsAuthenticated(true);
        }
        setLoading(false);
    }, []);

    const login = (newToken) => {
        setToken(newToken);
        setIsAuthenticated(true);
        toast.success("Logged in successfully!");
    };

    const logout = async () => {
        const response = await logoutUser();
        //console.log("Logout response:", response);

        localStorage.removeItem("authToken");
        clearToken();
        setIsAuthenticated(false);
        toast.success("Logged out successfully!");
    }

   

    return (
        <AuthContext.Provider
            value={{
                
                isAuthenticated,
                login,
                logout,
                loading
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }
    return context;
};
