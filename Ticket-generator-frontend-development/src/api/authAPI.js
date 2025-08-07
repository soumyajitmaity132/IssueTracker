import { apiClient, API_ENDPOINTS } from "./config";

export const authAPI = {
  login: async (email, password) => {
    const response = await apiClient.post(API_ENDPOINTS.AUTH.LOGIN, {
      email,
      password,
    });
    return response.data;
  },

  logout: async () => {
    return apiClient.post(API_ENDPOINTS.AUTH.LOGOUT);
  },

  getUserData: async () => {
    const response = await apiClient.get(API_ENDPOINTS.AUTH.VERIFY_TOKEN);
    return response.data;
  },
};
