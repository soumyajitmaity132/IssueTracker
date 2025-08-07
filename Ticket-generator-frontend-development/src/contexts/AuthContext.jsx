import React, { createContext, useContext, useEffect, useState } from "react";
import { authAPI } from "../api/authAPI";
import { ticketAPI } from "../api/ticketAPI";

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [subject , setSubject ] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkAuthStatus();
    fetchSubjects();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const token = localStorage.getItem("authToken");
      const storedUser = localStorage.getItem("user");
      
      if (token && storedUser) {
        try {
          const parsedUser = JSON.parse(storedUser);
          setUser(parsedUser);
        } catch (parseError) {
          console.error("Error parsing stored user:", parseError);
          if (token) {
            const userData = await authAPI.getUserData();
            const normalizedRole = userData.role === "ADMIN" ? "admin" : "user";
            const normalizedUser = { ...userData, role: normalizedRole };
            setUser(normalizedUser);
            localStorage.setItem("user", JSON.stringify(normalizedUser));
          }
        }
      } else if (token) {
        const userData = await authAPI.getUserData();
        const normalizedRole = userData.role === "ADMIN" ? "admin" : "user";
        const normalizedUser = { ...userData, role: normalizedRole };
        setUser(normalizedUser);
        localStorage.setItem("user", JSON.stringify(normalizedUser));
      }
    } catch (err) {
      console.error("Failed to check auth status:", err);
      localStorage.removeItem("authToken");
      localStorage.removeItem("user");
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authAPI.login(email, password);
      
      console.log("Login response:", response);

      if (response.token && response.role) {
        localStorage.setItem("authToken", response.token);
        
        const userData = await authAPI.getUserData();
        const normalizedRole = response.role === "ADMIN" ? "admin" : "user";
        const normalizedUser = { ...userData, role: normalizedRole };
        
        localStorage.setItem("user", JSON.stringify(normalizedUser));
        setUser(normalizedUser);
        
        console.log("User logged in:", normalizedUser);
        
        return { success: true, user: normalizedUser };
      }

      throw new Error("Invalid Login response");
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || error.message || "Login failed";
      setError(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      localStorage.removeItem("authToken");
      localStorage.removeItem("user");
      setUser(null);
      setError(null);
      window.location.href = "/login";
    }
  };

  const fetchSubjects = async () =>{
    try {
      const response  = await ticketAPI.getSubjects();
      setSubject(response.data);
      console.log(response.data);
      return response.data;
    } catch (error) {
      console.log(error);
    }
  } 

  const value = {
    user,
    loading,
    subject,
    error,
    login,
    logout,
    isAuthenticated: !!user,
    isAdmin: user?.role?.toLowerCase() === "admin",
    isUser: user?.role?.toLowerCase() === "user",
    clearError: () => setError(null),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};