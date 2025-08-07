import { NavLink } from 'react-router-dom';
import React, { useState } from "react";
import { 
  Ticket, 
  Users, 
  Plus, 
  FileText, 
  CheckSquare, 
  Trash2,
  Home
} from "lucide-react";

function Sidebar({ user }) {
  const [activeItem, setActiveItem] = useState('/dashboard/newticket');

  const adminNavItems = [
    { path: '/dashboard/homepage', icon: Home, label: 'Dashboard'},
    { path: '/dashboard/tickets', icon: Ticket, label: 'All Tickets'},
    { path: '/dashboard/employeelist', icon: Users, label: 'Employees'},
  ];

  const userNavItems = [
    { path: '/dashboard/homepage', icon: Home, label: 'Dashboard'},
    { path: '/dashboard/newticket', icon: Plus, label: 'New Ticket' },
    { path: '/dashboard/raisedticket', icon: FileText, label: 'Raised Tickets' },
    { path: '/dashboard/assignedticket', icon: CheckSquare, label: 'Assigned Tickets'},
    { path: '/dashboard/closedticket', icon: Trash2, label: 'Closed Tickets'},
  ];

  const navItems = user === 'admin' ? adminNavItems : userNavItems;

  const NavItem = ({ item }) => {
    return (
      <NavLink
        to={item.path}
        className={({ isActive }) =>
          `w-full flex items-center justify-between p-3 rounded-lg transition-all duration-200 ${
            isActive
              ? "bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-lg"
              : "text-gray-700 hover:bg-blue-50 hover:text-blue-700"
          }`
        }
      >
        <div className="flex items-center gap-3">
          <item.icon size={20} className="text-inherit" />
          <span className="font-medium">{item.label}</span>
        </div>
      </NavLink>
    );
  };

  return (
    <div className="fixed top-16 left-0 h-[calc(100vh-4rem)] w-64 bg-white shadow-xl border-r border-gray-200 z-40">
      <div className="p-6">
        <div className="mb-6">
          <div className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-sm font-medium ${
            user === 'admin' 
              ? 'bg-purple-100 text-purple-700' 
              : 'bg-green-100 text-green-700'
          }`}>
            <div className={`w-2 h-2 rounded-full ${
              user === 'admin' ? 'bg-purple-500' : 'bg-green-500'
            }`} />
            {user === 'admin' ? 'Administrator' : 'Employee'}
          </div>
        </div>

        <nav className="space-y-2">
          {navItems.map((item) => (
            <NavItem key={item.path} item={item} />
          ))}
        </nav>
      </div>
    </div>
  );
}

export default Sidebar;
