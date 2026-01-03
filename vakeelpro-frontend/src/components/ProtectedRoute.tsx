// src/components/ProtectedRoute.tsx
import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { RootState } from '../store/store';

const ProtectedRoute: React.FC<{ privilege?: string }> = ({ privilege }) => {
  const { user, token } = useSelector((state: RootState) => state.auth);

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (privilege) {
    const [feature, action] = privilege.split('_');
    if (!user?.featurePrivileges[feature]?.includes(action)) {
      return <Navigate to="/dashboard" />;
    }
  }

  return <Outlet />;
};

export default ProtectedRoute;