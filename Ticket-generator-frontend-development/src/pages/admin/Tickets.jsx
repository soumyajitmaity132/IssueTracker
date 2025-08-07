import React, { useEffect, useState } from "react";
import TicketsTable from "../../components/common/TicketsTable";
import { ticketAPI } from "../../api/ticketAPI";
import { Ticket } from 'lucide-react';

function Tickets() {
  const temp = [
    {
      ticketNo: 8,
      employeeName: "Pooja",
      empId: "1004",
      department: "Transport",
      subject: "Long Cab Hours",
      detailedMessage: "Cab Takes Long Routes",
      assignee: null,
      status: "OPEN",
      priority: "HIGH",
      createdAt: "2025-08-05T05:39:40.433008",
    }
  ];

  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const data = await ticketAPI.getAllTickets();
        if (data && data.length > 0) {
          setTickets(data);
        } else {
          setTickets(temp); 
        }
      } catch (error) {
        console.error("Failed to fetch tickets:", error);
        setTickets(temp); 
      } finally {
        setLoading(false);
      }
    };
    fetchTickets();
  }, []);

  if (loading) {
    return <p className="text-gray-500">Loading tickets...</p>;
  }

  return (
    <>
      <div className="mb-6">
        <div className="flex items-center gap-2 mb-2">
          <Ticket className="w-6 h-6 text-indigo-600" />
          <h2 className="text-2xl font-bold text-gray-800">Tickets</h2>
        </div>
        <div className="h-1 w-20 bg-gradient-to-r from-blue-600 to-indigo-600 rounded" />
      </div>

      <TicketsTable tickets={tickets} />
    </>
  );
}

export default Tickets;
