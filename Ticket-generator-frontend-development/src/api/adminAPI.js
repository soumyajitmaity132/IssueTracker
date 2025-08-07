import { apiClient, API_ENDPOINTS } from "./config";

export const adminAPI = {
  getEmployees: async () => {
    const response = await apiClient.get(API_ENDPOINTS.ADMIN.EMPLOYEES);
    return response.data;
  },

  addEmployee: async (user) => {
    const response = await apiClient.post(API_ENDPOINTS.ADMIN.ADD, {
      empId: user.empId,
      name: user.name,
      email: user.email,
      department: user.department,
      reportingManager: user.reportingManager,
      role: user.role,
      password: user.password,
    });
    return response.data;
  },

  addSubject: async (subjectData) => {
    const response = await apiClient.post(API_ENDPOINTS.ADMIN.SUBJECT, {
      department: subjectData.department,
      subject: subjectData.subject,
    });
    return response.data;
  }
};
