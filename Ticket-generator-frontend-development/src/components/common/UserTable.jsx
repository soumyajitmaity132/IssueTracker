import React from 'react';
import { Trash2, Edit3 } from 'lucide-react';
import { Link } from 'react-router-dom';

function UserTable({ employees = [] }) {
  const userHeaders = ["Employee ID", "Name", "Email", "Department", "Role", "Actions"];

  return (
    <div className="mb-12">
      

      <div className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gradient-to-r from-blue-50 to-indigo-50">
              <tr>
                {userHeaders.map((header, index) => (
                  <th
                    key={index}
                    className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider border-b border-gray-200"
                  >
                    {header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {employees.map((emp) => (
                <tr key={emp.empId} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-4 whitespace-nowrap font-semibold text-indigo-600">
                    {emp.empId.toString().padStart(6, '0')}
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-800">{emp.name}</td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">{emp.email}</td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">{emp.department}</td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">{emp.role}</td>
                  <td className="px-4 py-4 whitespace-nowrap">
                    <div className="flex gap-3">
                      <Link to={`/dashboard/addemployee/${emp.empId}`} className="text-blue-600 hover:text-blue-800 transition" title="Edit">
                        <Edit3 className="w-4 h-4" />
                      </Link>
                      <button className="text-red-600 hover:text-red-800 transition" title="Delete">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {employees.length === 0 && (
                <tr>
                  <td colSpan={userHeaders.length} className="px-4 py-4 text-center text-gray-500">
                    No employees found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default UserTable;
