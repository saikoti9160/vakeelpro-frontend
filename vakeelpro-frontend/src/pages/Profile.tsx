import React from 'react';
import { useAuth } from '../context/AuthContext';

const Profile: React.FC = () => {
  const { user } = useAuth();
  return (
    <div className="p-6">
      <h1 className="text-3xl">Profile</h1>
      <p>Username: {user?.username}</p>
      <p>Roles: {user?.roles.join(', ')}</p>
    </div>
  );
};

export default Profile;