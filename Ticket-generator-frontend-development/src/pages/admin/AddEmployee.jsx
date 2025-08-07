import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { adminAPI } from "../../api/adminAPI";
import { useAuth } from "../../contexts/AuthContext";

function AddEmployee() {
  const { empId } = useParams(); 
  const navigate = useNavigate();
  const isEditMode = Boolean(empId);

  const {user} = useAuth();

  const [formData, setFormData] = useState({
    empId: "",
    name: "",
    email: "",
    password: "",
    department: user.department,
    reportingManager: user.empId,
    role: "EMPLOYEE",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  useEffect(() => {
    if (isEditMode) {
      const fetchEmployee = async () => {
        try {
          const data = await adminAPI.getEmployeeById(empId);
          setFormData({
            empId: data.empId,
            name: data.name,
            email: data.email,
            department: data.department,
            reportingManager: data.reportingManager,
            role: data.role,
            password: "", 
          });
        } catch (error) {
          console.error("Failed to fetch employee:", error);
        }
      };
      fetchEmployee();
    }
  }, [empId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    // console.log(formData)
    try {
      if (isEditMode) {
        await adminAPI.updateEmployee(empId, formData);
        alert("Employee updated successfully");
      } else {
        await adminAPI.addEmployee(formData);
        alert("Employee added successfully");
      }
      navigate("/dashboard/employees");
    } catch (error) {
      console.error("Failed to submit:", error);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6 mt-5 bg-white shadow-md rounded-lg border border-gray-200">
      <h2 className="text-2xl font-semibold text-center mb-6 text-blue-600">
        👤 {isEditMode ? "Edit" : "Add"} Employee
      </h2>
      <form onSubmit={handleSubmit} className="space-y-5">
        {!isEditMode && (
          <div>
            <label className="block text-sm font-medium mb-1 text-gray-700">
              Employee ID
            </label>
            <input
              type="text"
              name="empId"
              placeholder="Enter Employee ID"
              value={formData.empId}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
              required
            />
          </div>
        )}

        <div>
          <label className="block text-sm font-medium mb-1 text-gray-700">
            Full Name
          </label>
          <input
            type="text"
            name="name"
            placeholder="Enter employee name"
            value={formData.name}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1 text-gray-700">
            Email Address
          </label>
          <input
            type="email"
            name="email"
            placeholder="example@company.com"
            value={formData.email}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
            required
          />
        </div>

        {!isEditMode && (
          <div>
            <label className="block text-sm font-medium mb-1 text-gray-700">
              Password
            </label>
            <input
              type="password"
              name="password"
              placeholder="********"
              value={formData.password}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
              required
            />
          </div>
        )}

        <div>
          <label className="block text-sm font-medium mb-1 text-gray-700">
            Role
          </label>
          <select
            name="role"
            value={formData.role}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none"
          >
            <option value="EMPLOYEE">Employee</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        <div className="pt-4">
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition"
          >
            {isEditMode ? "Update" : "Add"} Employee
          </button>
        </div>
      </form>
    </div>
  );
}

export default AddEmployee;
