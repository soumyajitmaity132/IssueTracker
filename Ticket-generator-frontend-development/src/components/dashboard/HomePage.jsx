import React from 'react'
import { 
  User, 
  LogOut, 
  Menu, 
  Ticket, 
  BarChart3, 
  Users, 
  FileText, 
  CheckSquare, 
} from "lucide-react";

function HomePage() {
  return (
    <>
      <div className="p-6">
          {/* Page Header */}
          <div className="mb-8">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <h1 className="text-2xl font-bold text-gray-800 mb-2">
                Welcome back, Deepak! 👋
              </h1>
              <p className="text-gray-600">
                Here's what's happening with your tickets today.
              </p>
            </div>
          </div>

          {/* Quick Action Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Open Tickets</p>
                  <p className="text-2xl font-bold text-gray-900">3</p>
                </div>
                <div className="bg-blue-100 p-3 rounded-lg">
                  <Ticket className="w-6 h-6 text-blue-600" />
                </div>
              </div>
              <p className="text-xs text-gray-500 mt-2">2 high priority</p>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Assigned</p>
                  <p className="text-2xl font-bold text-gray-900">2</p>
                </div>
                <div className="bg-green-100 p-3 rounded-lg">
                  <CheckSquare className="w-6 h-6 text-green-600" />
                </div>
              </div>
              <p className="text-xs text-gray-500 mt-2">In progress</p>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Resolved</p>
                  <p className="text-2xl font-bold text-gray-900">15</p>
                </div>
                <div className="bg-purple-100 p-3 rounded-lg">
                  <BarChart3 className="w-6 h-6 text-purple-600" />
                </div>
              </div>
              <p className="text-xs text-gray-500 mt-2">This month</p>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Avg Response</p>
                  <p className="text-2xl font-bold text-gray-900">2.3h</p>
                </div>
                <div className="bg-orange-100 p-3 rounded-lg">
                  <Users className="w-6 h-6 text-orange-600" />
                </div>
              </div>
              <p className="text-xs text-gray-500 mt-2">24h target</p>
            </div>
          </div>

          {/* Content Area */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="text-center py-12">
              <div className="bg-gray-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                <FileText className="w-8 h-8 text-gray-400" />
              </div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">
                Select a menu item to get started
              </h3>
              <p className="text-gray-600">
                Choose from the sidebar to view tickets, create new ones, or access reports.
              </p>
            </div>
          </div>
        </div>
    </>
  )
}

export default HomePage;
