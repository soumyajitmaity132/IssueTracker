import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { ticketAPI } from "../../api/ticketAPI"; 
import { adminAPI } from "../../api/adminAPI"; 

function NewTicket() {
  const { id } = useParams(); 
  const para = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);
  const { user } = useAuth();

  const [formData, setFormData] = useState({
    subject: "",
    detailedMessage: "",
    department: user.department,
    priority: "MEDIUM",
    status: "OPEN",
    assignedTo: "",
  });

  const [employees, setEmployees] = useState([]);
  const [subjects, setSubjects] = useState([]);

  useEffect(() => {
    if (isEditMode) {
      const fetchTicket = async () => {
        try {
          const data = await ticketAPI.getTicket(id);
          console.log(data)
          setFormData({
            subject: data.subject,
            detailedMessage: data.detailedMessage,
            priority: data.priority,
            severity: data.severity,
            status: data.status,
            assignedTo: data.assignedTo || "",
          });
        } catch (err) {
          console.error("Error fetching ticket:", err);
        }
      };
      fetchTicket();
    }

    ticketAPI
      .getSubjects()
      .then(setSubjects)
      .catch((err) => console.error("Error fetching subjects:", err));

    if (user.role === "ADMIN") {
      adminAPI.getEmployees().then(setEmployees).catch(console.error);
    }
  }, [id, user.role]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleAssignToMe = () => {
    setFormData((prev) => ({ ...prev, assignedTo: user.empId }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditMode) {
        await ticketAPI.updateTicket(id, formData);
        alert("Ticket updated successfully");
      } else {
        await ticketAPI.addTicket(formData);
        alert("Ticket created successfully");
      }
      navigate("/dashboard/");
    } catch (err) {
      console.error("Ticket submit error:", err);
    }
  };

  return (
    <div className="max-w-2xl mx-auto mt-10 p-6 bg-white shadow-md border border-gray-200 rounded-lg">
      <h2 className="text-2xl font-bold text-center text-blue-600 mb-6">
        {isEditMode ? "✏️ Edit Ticket" : "📝 Submit New Ticket"}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Subject
          </label>
          <select
            name="subject"
            value={formData.subject}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
          >
            <option value="">-- Select Subject --</option>
            {subjects.map((sub, index) => (
              <option key={index} value={sub}>
                {sub}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            name="detailedMessage"
            rows="4"
            placeholder="Describe the issue..."
            value={formData.detailedMessage}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1 text-gray-700">
            Priority
          </label>
          <select
            name="priority"
            value={formData.priority}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-400 focus:ring-2 focus:outline-none"
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>
        </div>

        {isEditMode && (
          <div>
            <label className="block text-sm font-medium mb-1 text-gray-700">
              Status
            </label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
            >
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>
        )}

        <div className="pt-4">
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition"
          >
            {isEditMode ? "Update Ticket" : "Submit Ticket"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default NewTicket;
