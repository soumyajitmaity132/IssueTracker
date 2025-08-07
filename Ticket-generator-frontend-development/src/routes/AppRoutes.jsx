import { Routes, Route, Navigate } from "react-router-dom";
import Login from "../components/authentication/Login";
import EmployeeList from "../pages/admin/EmployeeList";
import Tickets from "../pages/admin/Tickets";
import Dashboard from "../components/dashboard/Dashboard";
import AddEmployee from "../pages/admin/AddEmployee";
import AssignedTickets from "../pages/employee/AssignedTickets";
import RaisedTickets from "../pages/employee/RaisedTickets";
import NewTicket from "../pages/employee/NewTicket";
import HomePage from "../components/dashboard/HomePage";
import ClosedTickets from "../pages/employee/ClosedTickets";
import ProtectedRoute from "../components/common/ProtectedRoute";
import TicketView from "../pages/TicketView";

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/login" element={<Login />} />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute allowedRoles={["admin", "user"]}>
            <Dashboard />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="homepage" replace />} />
        
        <Route path="homepage" element={<HomePage />} />
        <Route path="newticket" element={<NewTicket />} />
        <Route path="raisedticket" element={<RaisedTickets />} />
        <Route path="assignedticket" element={<AssignedTickets />} />
        <Route path="closedticket" element={<ClosedTickets />} />
        <Route path="editticket/:id" element={<NewTicket />} />
        <Route path="viewticket/:id" element={<TicketView />} />
        
        <Route
          path="employeelist"
          element={
            <ProtectedRoute allowedRoles={["admin"]}>
              <EmployeeList />
            </ProtectedRoute>
          }
        />
        <Route path="tickets" element={<ProtectedRoute allowedRoles={["admin"]}>
              <Tickets />
            </ProtectedRoute>} />

        <Route
          path="addemployee"
          element={
            <ProtectedRoute allowedRoles={["admin"]}>
              <AddEmployee />
            </ProtectedRoute>
          }
        />
        <Route
          path="addemployee/:id"
          element={
            <ProtectedRoute allowedRoles={["admin"]}>
              <AddEmployee />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route
        path="/unauthorized"
        element={
          <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
              <div className="text-6xl text-red-500 mb-4">🚫</div>
              <h1 className="text-3xl font-bold text-red-600 mb-2">Access Denied</h1>
              <p className="text-gray-600 mb-4">You don't have permission to access this page.</p>
              <button
                onClick={() => window.history.back()}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
              >
                Go Back
              </button>
            </div>
          </div>
        }
      />
      
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default AppRoutes;