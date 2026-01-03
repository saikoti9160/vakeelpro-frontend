// src/api/api.ts
export const API_BASE_URL = 'http://localhost:8085/api/auth';

export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/login`,
  SIGNUP: `${API_BASE_URL}/signup`,
  FORGOT_PASSWORD: `${API_BASE_URL}/forgot-password`,
  VERIFY: `${API_BASE_URL}/verify`,
  CHANGE_PASSWORD: `${API_BASE_URL}/change-password`,
};