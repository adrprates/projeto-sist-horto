import { useState, useEffect } from 'react';
import type{ ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import { AuthContext } from './AuthContext';

interface User {
  sub: string;
  role: string;
  exp?: number;
}

interface AuthContextType {
  token: string | null;
  user: User | null;
  loading: boolean;
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (roles: string[]) => boolean;
}

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    try {
      const storedToken = localStorage.getItem('authToken');
      if (storedToken) {
        const decodedToken: User = jwtDecode(storedToken);
        
        const isExpired = decodedToken.exp ? decodedToken.exp * 1000 < Date.now() : false;

        if (isExpired) {
          localStorage.removeItem('authToken'); 
        } else {
          setUser({
            sub: decodedToken.sub,
            role: decodedToken.role.replace('ROLE_', '')
          });
          setToken(storedToken);
        }
      }
    } catch (error) {
      console.error("Token inválido ou expirado, limpando sessão:", error);
      localStorage.removeItem('authToken');
    } finally {
      setLoading(false);
    }
  }, []);

  const login = (newToken: string) => {
    localStorage.setItem('authToken', newToken);
    setToken(newToken);
    const decodedToken: User = jwtDecode(newToken);
    setUser({
      sub: decodedToken.sub,
      role: decodedToken.role.replace('ROLE_', '')
    });
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setToken(null);
    setUser(null);
  };

  const hasRole = (roles: string[]): boolean => {
    return user ? roles.includes(user.role) : false;
  };

  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider value={{ token, user, loading, login, logout, isAuthenticated, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export type { User, AuthContextType };