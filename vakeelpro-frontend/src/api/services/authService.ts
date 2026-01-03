// src/api/services/authService.ts
import axiosInstance from '../axiosInstance';
import { API_ENDPOINTS } from '../api';
import { LoginRequest, LoginResponse, SignupRequest, SignupResponse } from '../../types/auth';

export interface AuthService {
  login: (data: LoginRequest) => Promise<LoginResponse>;
  signup: (data: SignupRequest) => Promise<SignupResponse>;
  forgotPassword: (email: string) => Promise<void>;
  verifyAccount: (token: string) => Promise<void>;
  changePassword: (currentPassword: string, newPassword: string) => Promise<void>;
}

export const authService: AuthService = {
  login: async (data: LoginRequest) => {
    const response = await axiosInstance.post<LoginResponse>(API_ENDPOINTS.LOGIN, data);
    return response.data;
  },
  signup: async (data: SignupRequest) => {
    const response = await axiosInstance.post<SignupResponse>(API_ENDPOINTS.SIGNUP, data);
    return response.data;
  },
  forgotPassword: async (email: string) => {
    await axiosInstance.post(API_ENDPOINTS.FORGOT_PASSWORD, null, { params: { email } });
  },
  verifyAccount: async (token: string) => {
    await axiosInstance.get(API_ENDPOINTS.VERIFY, { params: { token } });
  },
  changePassword: async (currentPassword: string, newPassword: string) => {
    await axiosInstance.post(API_ENDPOINTS.CHANGE_PASSWORD, null, {
      params: { currentPassword, newPassword },
    });
  },
};