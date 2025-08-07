import React, { useState, useEffect } from 'react';
import { ArrowLeft, User, Calendar, Clock, MessageCircle, Send, Edit3, CheckCircle } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useParams, useNavigate } from 'react-router-dom';
import { ticketAPI } from '../api/ticketAPI';

function TicketView() {
  const { user, isAdmin } = useAuth();
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const currentUser = user;

  // Initial mock data - replace with API call
  const [ticket, setTicket] = useState({
    ticketNo: 12345,
    employeeName: 'John Doe',
    empId: 'EMP001',
    department: 'IT',
    subject: 'System Access Issue',
    detailedMessage: 'Unable to access the customer management system since yesterday. Getting authentication errors when trying to log in. This is blocking my daily work activities.',
    assignee: 'Jane Smith',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    createdAt: '2024-01-15T10:30:00'
  });

  const [comments, setComments] = useState([
    {
      id: 1,
      author: 'Jane Smith',
      message: 'I have received your ticket and will start investigating the authentication issue.',
      timestamp: '2024-01-15T11:00:00',
      isAdmin: true
    },
    {
      id: 2,
      author: 'John Doe',
      message: 'Thank you for the quick response. I tried clearing browser cache as suggested but the issue persists.',
      timestamp: '2024-01-15T14:30:00',
      isAdmin: false
    }
  ]);

  // Mock users list for admin assignment
  const [users, setUsers] = useState([
    { id: 1, name: 'Jane Smith', empId: 'EMP002' },
    { id: 2, name: 'Mike Johnson', empId: 'EMP003' },
    { id: 3, name: 'Sarah Wilson', empId: 'EMP004' }
  ]);

  const [newComment, setNewComment] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [selectedAssignee, setSelectedAssignee] = useState(ticket.assignee);
  const [selectedStatus, setSelectedStatus] = useState(ticket.status);
  const [loading, setLoading] = useState(true);

  const statusOptions = [
    { value: 'OPEN', label: 'Open' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'RESOLVED', label: 'Resolved' },
    { value: 'CLOSED', label: 'Closed' }
  ];

  const priorityColors = {
    HIGH: 'border-red-500 text-red-600 bg-red-50',
    MEDIUM: 'border-yellow-500 text-yellow-600 bg-yellow-50',
    LOW: 'border-green-500 text-green-600 bg-green-50'
  };

  const statusColors = {
    OPEN: 'border-blue-500 text-blue-600 bg-blue-50',
    IN_PROGRESS: 'border-yellow-500 text-yellow-600 bg-yellow-50',
    RESOLVED: 'border-green-500 text-green-600 bg-green-50',
    CLOSED: 'border-gray-500 text-gray-600 bg-gray-50'
  };

  // Fetch ticket data when component mounts
  useEffect(() => {
    const fetchTicketData = async () => {
      if (ticketId) {
        try {
          const ticketData = await ticketAPI.getTicket(ticketId);
          if (ticketData) {
            setTicket(ticketData);
            setSelectedAssignee(ticketData.assignee);
            setSelectedStatus(ticketData.status);
          }
        } catch (error) {
          console.error('Failed to fetch ticket:', error);
        } finally {
          setLoading(false);
        }
      } else {
        setLoading(false);
      }
    };

    fetchTicketData();
  }, [ticketId]);

  const handleCommentSubmit = (e) => {
    if (e) e.preventDefault();
    if (newComment.trim()) {
      const comment = {
        id: comments.length + 1,
        author: currentUser?.name || 'Current User',
        message: newComment,
        timestamp: new Date().toISOString(),
        isAdmin: isAdmin
      };
      setComments([...comments, comment]);
      setNewComment('');
    }
  };

  const handleAssignToMe = () => {
    setSelectedAssignee(currentUser?.name || 'Current User');
    setTicket({ ...ticket, assignee: currentUser?.name || 'Current User' });
  };

  const handleSaveChanges = async () => {
    try {
      const updatedTicket = {
        ...ticket,
        assignee: selectedAssignee,
        status: selectedStatus
      };
      
      // Update via API
      await ticketAPI.updateTicket(ticketId, updatedTicket);
      
      setTicket(updatedTicket);
      setIsEditing(false);
    } catch (error) {
      console.error('Failed to update ticket:', error);
      // Handle error - maybe show a toast notification
    }
  };

  const handleGoBack = () => {
    navigate(-1); // Go back to previous page
  };

  const canModifyTicket = isAdmin || (currentUser?.name === ticket.assignee);

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading ticket details...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b p-2">
      <div className="mx-auto">
        {/* Header */}
        <div className="mb-6 flex items-center gap-4">
          <button 
            onClick={handleGoBack}
            className="flex items-center gap-2 text-indigo-600 hover:text-indigo-800 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-800">
            Ticket #{ticket.ticketNo.toString().padStart(5, '0')}
          </h1>
        </div>

        <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
          {/* Main Ticket Details */}
          <div className="xl:col-span-2 space-y-6">
            {/* Ticket Information Card */}
            <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
              <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-4 mb-6">
                <h2 className="text-xl sm:text-2xl font-semibold text-gray-800">{ticket.subject}</h2>
                <div className="flex flex-wrap gap-2">
                  <span className={`px-3 py-1 rounded-full text-sm font-medium border ${priorityColors[ticket.priority]}`}>
                    {ticket.priority}
                  </span>
                  <span className={`px-3 py-1 rounded-full text-sm font-medium border ${statusColors[ticket.status]}`}>
                    {ticket.status.replace('_', ' ')}
                  </span>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6 text-sm">
                <div className="flex items-center gap-2">
                  <User className="w-4 h-4 text-gray-400 flex-shrink-0" />
                  <span className="text-gray-600">Employee:</span>
                  <span className="font-medium">{ticket.employeeName} ({ticket.empId})</span>
                </div>
                <div className="flex items-center gap-2">
                  <Calendar className="w-4 h-4 text-gray-400 flex-shrink-0" />
                  <span className="text-gray-600">Department:</span>
                  <span className="font-medium">{ticket.department}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Clock className="w-4 h-4 text-gray-400 flex-shrink-0" />
                  <span className="text-gray-600">Created:</span>
                  <span className="font-medium">{new Date(ticket.createdAt).toLocaleString()}</span>
                </div>
                <div className="flex items-center gap-2">
                  <User className="w-4 h-4 text-gray-400 flex-shrink-0" />
                  <span className="text-gray-600">Assigned to:</span>
                  <span className="font-medium">{ticket.assignee || 'Unassigned'}</span>
                </div>
              </div>

              <div className="border-t pt-6">
                <h3 className="font-medium text-gray-800 mb-3">Description</h3>
                <p className="text-gray-600 leading-relaxed">{ticket.detailedMessage}</p>
              </div>
            </div>

            {/* Comments Section */}
            <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
              <div className="flex items-center gap-2 mb-6">
                <MessageCircle className="w-5 h-5 text-indigo-600" />
                <h3 className="text-lg font-semibold text-gray-800">Comments ({comments.length})</h3>
              </div>

              <div className="space-y-4 mb-6 max-h-96 overflow-y-auto">
                {comments.map((comment) => (
                  <div key={comment.id} className={`p-4 rounded-lg border-l-4 ${comment.isAdmin ? 'border-indigo-500 bg-indigo-50' : 'border-gray-300 bg-gray-50'}`}>
                    <div className="flex justify-between items-start mb-2">
                      <div className="flex items-center gap-2">
                        <span className="font-medium text-gray-800">{comment.author}</span>
                        {comment.isAdmin && (
                          <span className="text-xs bg-indigo-100 text-indigo-700 px-2 py-1 rounded-full">Admin</span>
                        )}
                      </div>
                      <span className="text-xs text-gray-500 flex-shrink-0">
                        {new Date(comment.timestamp).toLocaleString()}
                      </span>
                    </div>
                    <p className="text-gray-700">{comment.message}</p>
                  </div>
                ))}
              </div>

              {/* Add Comment Form */}
              <div className="border-t pt-4">
                <div className="flex flex-col sm:flex-row gap-3">
                  <div className="flex-1">
                    <textarea
                      value={newComment}
                      onChange={(e) => setNewComment(e.target.value)}
                      placeholder="Add a comment..."
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent resize-none"
                      rows="3"
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
                          handleCommentSubmit(e);
                        }
                      }}
                    />
                  </div>
                  <button
                    onClick={handleCommentSubmit}
                    disabled={!newComment.trim()}
                    className="self-end px-4 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2 whitespace-nowrap"
                  >
                    <Send className="w-4 h-4" />
                    Send
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Assignment & Status Card */}
            <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
              <h3 className="text-lg font-semibold text-gray-800 mb-4">Ticket Management</h3>

              {!isEditing ? (
                <div className="space-y-4">
                  {/* Assign to Me Button (for users) */}
                  {!isAdmin && (
                    <button
                      onClick={handleAssignToMe}
                      className="w-full px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors flex items-center justify-center gap-2"
                    >
                      <User className="w-4 h-4" />
                      Assign to Me
                    </button>
                  )}

                  {/* Edit Button (for admin or assigned user) */}
                  {canModifyTicket && (
                    <button
                      onClick={() => setIsEditing(true)}
                      className="w-full px-4 py-2 border-2 border-indigo-600 text-indigo-600 rounded-lg hover:bg-indigo-50 transition-colors flex items-center justify-center gap-2"
                    >
                      <Edit3 className="w-4 h-4" />
                      Edit Assignment & Status
                    </button>
                  )}
                </div>
              ) : (
                <div className="space-y-4">
                  {/* Assignment Dropdown (Admin only) */}
                  {isAdmin && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Assign to:</label>
                      <select
                        value={selectedAssignee}
                        onChange={(e) => setSelectedAssignee(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                      >
                        <option value="">Unassigned</option>
                        {users.map((user) => (
                          <option key={user.id} value={user.name}>
                            {user.name} ({user.empId})
                          </option>
                        ))}
                      </select>
                    </div>
                  )}

                  {/* Status Dropdown */}
                  {canModifyTicket && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Status:</label>
                      <select
                        value={selectedStatus}
                        onChange={(e) => setSelectedStatus(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                      >
                        {statusOptions.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                    </div>
                  )}

                  <div className="flex gap-2">
                    <button
                      onClick={handleSaveChanges}
                      className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
                    >
                      <CheckCircle className="w-4 h-4" />
                      Save
                    </button>
                    <button
                      onClick={() => {
                        setIsEditing(false);
                        setSelectedAssignee(ticket.assignee);
                        setSelectedStatus(ticket.status);
                      }}
                      className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TicketView;