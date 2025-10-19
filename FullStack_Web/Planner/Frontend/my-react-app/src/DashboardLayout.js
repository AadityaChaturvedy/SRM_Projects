import React from 'react';
import { useNavigate } from 'react-router-dom';
import './style.css';
import TimetablePanel from './TimetablePanel';
import TasksPanel from './TasksPanel';
import TodoPanel from './TodoPanel';

const DashboardLayout = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('sessionToken');
    navigate('/');
  };

  return (
    <>
      <header className="dashboard-header">
        <div className="header-content">
          <span className="icon">✨</span>
          <h1 className="app-title">PlanWise</h1>
          <span className="icon">🗓️</span>
          <button onClick={handleLogout} style={{ marginLeft: 'auto', padding: '8px 15px', backgroundColor: '#f44336', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Logout</button>
        </div>
      </header>

      <main className="dashboard-main">
        <TimetablePanel />
        <div className="side-panels">
          <TasksPanel />
          <TodoPanel />
        </div>
      </main>
    </>
  );
};

export default DashboardLayout;
