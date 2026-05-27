import { useState, useCallback, type ReactNode } from 'react';
import { api } from '../api/client';
import { AuthContext } from './auth';

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));

  const login = useCallback(async (email: string, password: string) => {
    const res = await api.post<{ token: string }>('/auth/login', { email, password });
    localStorage.setItem('token', res.token);
    setToken(res.token);
  }, []);

  const register = useCallback(async (name: string, email: string, password: string) => {
    await api.post('/auth/register', { name, email, password });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    setToken(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, login, register, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}
