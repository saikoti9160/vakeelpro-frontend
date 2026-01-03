import React from 'react';
import { useAuth } from '../context/AuthContext';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  return (
    <div className="p-6">
      <h1 className="text-3xl">Dashboard</h1>
      <p>Welcome, {user?.username}!</p>
    </div>
  );
};

export default Dashboard;