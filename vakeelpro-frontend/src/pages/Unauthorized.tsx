import React from 'react';

const Unauthorized: React.FC = () => {
  return (
    <div className="p-6">
      <h1 className="text-3xl">Unauthorized</h1>
      <p>You do not have permission to access this page.</p>
    </div>
  );
};

export default Unauthorized;