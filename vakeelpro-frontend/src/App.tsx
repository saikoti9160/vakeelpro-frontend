import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Box } from '@mui/material';
import Login from './components/Login';
import Sidebar from './components/Sidebar'; // Already imported
import ProtectedRoute from './components/ProtectedRoute';
import CasesDashboard from './pages/CasesDashboard';
import CompanyProfile from './pages/CompanyProfile';
import OrgAccount from './pages/OrgAccount';
import { RootState } from './store/store';
import { setAuth } from './store/authSlice'; // Adjusted path if needed
import { AuthProvider } from './context/AuthContext';

const App: React.FC = () => {
  const dispatch = useDispatch();
  const { user, token } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken && !user) {
      // Simulate fetching user data from token (in production, call an API)
      dispatch(
        setAuth({
          user: {
            username: 'temp',
            token: storedToken,
            accountType: 'INDIVIDUAL',
            roles: [],
            featurePrivileges: {},
          },
          token: storedToken,
        })
      );
    }
  }, [dispatch, user]);

  return (
    <Router>
      <AuthProvider>
      <Box sx={{ display: 'flex' }}>
        {/* {token && <Sidebar />} */}
        <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route element={<ProtectedRoute />}>
              <Route path="/dashboard" element={<CasesDashboard />} />
              <Route path="/company-profile" element={<CompanyProfile />} />
              <Route path="/org-account" element={<OrgAccount />} />
            </Route>
            <Route path="*" element={<Navigate to={token ? '/dashboard' : '/login'} />} />
          </Routes>
        </Box>
      </Box>
      </AuthProvider>
    </Router>
  );
};

export default App;
