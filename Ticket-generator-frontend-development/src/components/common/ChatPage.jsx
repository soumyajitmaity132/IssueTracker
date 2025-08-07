import React, { useState } from "react";
import { MessageCircle, Calendar, User, Tag, Clock, Trash2, CheckCircle } from "lucide-react";

function ChatPage({ ticket, onBack }) {
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: 'system',
      message: `Chat initiated for ticket ${ticket.id}`,
      timestamp: new Date().toISOString(),
    },
    {
      id: 2,
      sender: 'support',
      message: 'Hello! I\'m here to help you with your ticket. How can I assist you today?',
      timestamp: new Date(Date.now() - 300000).toISOString(),
    }
  ]);
  const [newMessage, setNewMessage] = useState('');

  const handleSendMessage = () => {
    if (newMessage.trim()) {
      const message = {
        id: messages.length + 1,
        sender: 'user',
        message: newMessage.trim(),
        timestamp: new Date().toISOString(),
      };
      setMessages([...messages, message]);
      setNewMessage('');
      
      setTimeout(() => {
        const responses = [
          "Thank you for your message. I'm looking into this issue.",
          "I understand your concern. Let me check the system for you.",
          "That's a good point. I'll escalate this to the appropriate team.",
          "I've noted your feedback. Is there anything else I can help with?"
        ];
        const autoResponse = {
          id: messages.length + 2,
          sender: 'support',
          message: responses[Math.floor(Math.random() * responses.length)],
          timestamp: new Date().toISOString(),
        };
        setMessages(prev => [...prev, autoResponse]);
      }, 1500);
    }
  };

  const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const getSenderStyle = (sender) => {
    switch (sender) {
      case 'user':
        return 'bg-blue-600 text-white ml-auto';
      case 'support':
        return 'bg-gray-200 text-gray-800 mr-auto';
      case 'system':
        return 'bg-yellow-100 text-yellow-800 mx-auto text-center';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="fixed inset-0 bg-white z-50 flex flex-col">
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white p-4 shadow-lg">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="bg-white bg-opacity-20 hover:bg-opacity-30 p-2 rounded-lg transition-colors"
            >
              ←
            </button>
            <div>
              <h1 className="text-lg font-semibold">Chat - {ticket.id}</h1>
              <p className="text-blue-100 text-sm">{ticket.department} • {ticket.status}</p>
            </div>
          </div>
          <div className="text-right">
            <p className="text-sm text-blue-100">Support Chat</p>
            <div className="flex items-center gap-2 text-xs text-blue-200">
              <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
              Online
            </div>
          </div>
        </div>
      </div>

      <div className="bg-gray-50 border-b p-4">
        <div className="max-w-4xl mx-auto">
          <p className="text-sm text-gray-600 font-medium">Issue: {ticket.description}</p>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        <div className="max-w-4xl mx-auto space-y-4">
          {messages.map((msg) => (
            <div key={msg.id} className="flex flex-col">
              <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${getSenderStyle(msg.sender)}`}>
                <p className="text-sm">{msg.message}</p>
              </div>
              <span className="text-xs text-gray-500 mt-1 mx-2">
                {msg.sender !== 'system' && (
                  <span className="capitalize">{msg.sender === 'user' ? 'You' : 'Support'} • </span>
                )}
                {formatTime(msg.timestamp)}
              </span>
            </div>
          ))}
        </div>
      </div>

      <div className="border-t bg-white p-4">
        <div className="max-w-4xl mx-auto flex gap-3">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
            placeholder="Type your message..."
            className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
          />
          <button
            onClick={handleSendMessage}
            disabled={!newMessage.trim()}
            className="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white px-6 py-2 rounded-lg transition-colors flex items-center gap-2"
          >
            <MessageCircle className="w-4 h-4" />
            Send
          </button>
        </div>
      </div>
    </div>
  );
}

export default ChatPage;