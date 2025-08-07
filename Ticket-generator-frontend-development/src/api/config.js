import axios from "axios";

export const API_CONFIG = {
  BASE_URL: "http://localhost:8080/api",
  TIMEOUT: 10000,
  HEADERS: {
    "Content-Type": "application/json",
  },
};

export const apiClient = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: API_CONFIG.TIMEOUT,
  headers: API_CONFIG.HEADERS,
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("authToken");
      window.location.href = "/";
    } else if (error.response?.status === 403) {
      console.error("Access forbidden");
    } else if (error.response?.status >= 500) {
      console.error("Server error occurred");
    }

    return Promise.reject(error);
  }
);

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: "/auth/login",
    LOGOUT: "/auth/logout",
    VERIFY_TOKEN: "/auth/me",
    UPDATE_PROFILE: "/auth/update-profile",
  },
  ADMIN:{
    EMPLOYEES: "/admin/get_employees",
    ADD: "/admin/add_employees",
    SUBJECT: "/admin/tickets/add_subjects",
  },
  TICKET:{
    LIST:"/admin/tickets",
    RAISED:"/employee/tickets/raised",
    CLOSED:"/employee/tickets/closed",
    ASSIGNED:"/employee/tickets/assigned",
    FIXED:(id) => `/employee/tickets/assigned/${id}/fix`,
    REOPEN:(id) => `/employee/tickets/reopen/${id}`,
    ADD:"/employee/tickets/submit",
    SUBJECT:"/employee/tickets/get_subjects",
    GET:(id) => `/admin/tickets/${id}`,
    UPDATE:(id , key) => `/admin/tickets/${id}/assignee?assignee=${key}`,
    CLOSEDID:(id)=>`/admin/tickets/close/${id}`
  }
};