import { apiClient, API_ENDPOINTS } from "./config";

export const ticketAPI = {
  getAllTickets: async () => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.LIST);
    return response.data;
  },

  raisedTickets: async () => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.RAISED);
    return response.data;
  },

  closedTickets: async () => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.CLOSED);
    return response.data;
  },

  assignedTickets: async () => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.ASSIGNED);
    return response.data;
  },

  fixedTicket: async (ticketId, payload) => {
    const response = await apiClient.post(API_ENDPOINTS.TICKET.FIXED(ticketId), payload);
    return response.data;
  },

  reopenTicket: async (ticketId, payload) => {
    const response = await apiClient.post(API_ENDPOINTS.TICKET.REOPEN(ticketId), payload);
    return response.data;
  },

  addTicket: async (ticket) => {
    const response = await apiClient.post(API_ENDPOINTS.TICKET.ADD, {
      subject : ticket.subject,
      department : ticket.department,
      detailedMessage : ticket.detailedMessage,
      priority : ticket.priority
    });
    return response.data;
  },

  getSubjects: async () => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.SUBJECT);
    return response.data;
  },

  getTicket: async (id) => {
    const response = await apiClient.get(API_ENDPOINTS.TICKET.GET(id));
    return response.data;
  },

  updateAssignee: async (ticketId, assigneeId) => {
    const response = await apiClient.put(API_ENDPOINTS.TICKET.UPDATE(ticketId, assigneeId));
    return response.data;
  },

  closeTicket: async (ticketId) => {
    const response = await apiClient.put(API_ENDPOINTS.TICKET.CLOSED(ticketId));
    return response.data;
  }
};
