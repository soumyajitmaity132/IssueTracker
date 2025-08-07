import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import { User, LogOut, Ticket } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

function Navbar() {
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
  };

  return (
    <nav className="fixed top-0 left-0 w-full bg-white shadow-sm z-50 border-b border-gray-200">
      <div className="px-6 py-3">
        <div className="flex justify-between items-center">
          <div className="flex items-center gap-3">
            <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-2 rounded-lg">
              <Ticket size={24} className="text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-800">VBS Ticket Collector</h1>
              <p className="text-xs text-gray-500 hidden sm:block">Support Management System</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            {user && (
              <div className="relative">
                <button
                  onClick={() => setShowProfileMenu(!showProfileMenu)}
                  className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div className="bg-gradient-to-r from-blue-500 to-indigo-500 p-2 rounded-full">
                    <User size={18} className="text-white" />
                  </div>
                  <div className="text-left hidden sm:block">
                    <p className="font-medium text-gray-800">
                      {user.name}
                    </p>
                    <p className="text-xs text-gray-500">
                      {user.role === "admin" ? "Administrator" : "Employee"}
                    </p>
                  </div>
                </button>

                {showProfileMenu && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2">
                    <NavLink
                      to="/dashboard/homepage"
                      className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
                      onClick={() => setShowProfileMenu(false)}
                    >
                      <User size={16} />
                      Profile
                    </NavLink>
                    <button
                      onClick={handleLogout}
                      className="flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50 w-full text-left"
                    >
                      <LogOut size={16} />
                      Logout
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
