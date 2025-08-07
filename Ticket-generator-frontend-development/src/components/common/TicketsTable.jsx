import React from 'react';
import { Ticket, Edit3, Trash2, Eye } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

function TicketsTable({ tickets = [] }) {
  const headers = ['Ticket ID', 'Status', 'Priority', 'Department', 'Assignee', 'Created At', 'Actions'];

  return (
    <div className="mb-12">
      <div className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gradient-to-r from-blue-50 to-indigo-50">
              <tr>
                {headers.map((header, index) => (
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
              {tickets.map((ticket) => (
                <tr key={ticket.ticketNo} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-4 whitespace-nowrap font-semibold text-indigo-600">
                    {ticket.ticketNo.toString().padStart(5, '0')}
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium border 
                      ${ticket.status === 'OPEN' ? 'border-green-500 text-green-600' :
                        ticket.status === 'CLOSED' ? 'border-gray-400 text-gray-500' :
                        'border-yellow-500 text-yellow-600'}`}>
                      {ticket.status.charAt(0) + ticket.status.slice(1).toLowerCase()}
                    </span>
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium border 
                      ${ticket.priority === 'HIGH' ? 'border-red-500 text-red-600' :
                        ticket.priority === 'MEDIUM' ? 'border-yellow-500 text-yellow-600' :
                        'border-green-500 text-green-600'}`}>
                      {ticket.priority.charAt(0) + ticket.priority.slice(1).toLowerCase()}
                    </span>
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">{ticket.department}</td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">
                    {ticket.assignee || 'Unassigned'}
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap text-gray-700">
                    {ticket.createdAt ? new Date(ticket.createdAt).toLocaleDateString() : 'N/A'}
                  </td>
                  <td className="px-4 py-4 whitespace-nowrap">
                    <div className="flex gap-3">
                      <Link to={`/dashboard/viewticket/${ticket.ticketNo}`} className="text-blue-600 hover:text-blue-800 transition" title="View">
                        <Eye className="w-4 h-4" />
                      </Link>
                      <Link to={`/dashboard/editticket/${ticket.ticketNo}`} className="text-blue-600 hover:text-blue-800 transition" title="Edit">
                        <Edit3 className="w-4 h-4" />
                      </Link>
                      <button className="text-red-600 hover:text-red-800 transition" title="Delete">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {tickets.length === 0 && (
                <tr>
                  <td colSpan={headers.length} className="px-4 py-4 text-center text-gray-500">
                    No tickets found.
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

export default TicketsTable;
