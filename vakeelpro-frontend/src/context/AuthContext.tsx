import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';

interface User {
  username: string;
  roles: string[];
  featurePrivileges: Record<string, string[]>;
}

interface AuthContextType {
  isAuthenticated: boolean;
  token: string | null;
  user: User | null;
  login: (token: string) => void;
  logout: () => void;
  checkAuth: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const decodeJWT = (token: string) => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload;
  } catch (e) {
    return null;
  }
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const checkAuth = () => {
    const storedToken = localStorage.getItem('token');
    if (!storedToken) return false;

    const decoded = decodeJWT(storedToken);
    if (!decoded || (decoded.exp * 1000 < Date.now())) {
      logout();
      return false;
    }
    return true;
  };

  const login = (newToken: string) => {
    const decoded = decodeJWT(newToken);
    if (decoded) {
      const userData: User = {
        username: decoded.sub || '',
        roles: decoded.roles || [],
        featurePrivileges: decoded.featurePrivileges || {},
      };
      localStorage.setItem('token', newToken);
      setToken(newToken);
      setUser(userData);
      setIsAuthenticated(true);
      const redirectPath = userData.featurePrivileges.DASHBOARD?.length ? '/dashboard' : '/profile';
      navigate(redirectPath);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    navigate('/login');
  };

  useEffect(() => {
    const initAuth = () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken && checkAuth()) {
        const decoded = decodeJWT(storedToken);
        setToken(storedToken);
        setUser({
          username: decoded.sub || '',
          roles: decoded.roles || [],
          featurePrivileges: decoded.featurePrivileges || {},
        });
        setIsAuthenticated(true);
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, token, user, login, logout, checkAuth }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = React.useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};