// src/types/auth.ts
export interface LoginRequest {
    username: string;
    password: string;
  }
  
  export interface LoginResponse {
    token: string;
    username: string;
    accountType: string;
    roles: string[];
    featurePrivileges: Record<string, string[]>;
    organization?: {
      name: string;
      address: string;
      lawFirmData: string;
    };
  }
  
  export interface SignupRequest {
    username: string;
    password: string;
    email: string;
    accountType: string;
    organization?: {
      name: string;
      address: string;
      lawFirmData: string;
    };
  }
  
  export interface SignupResponse {
    username: string;
    email: string;
    accountType: string;
    message: string;
  }
  
  export interface User {
    username: string;
    token: string;
    accountType: string;
    roles: string[];
    featurePrivileges: Record<string, string[]>;
    organization?: {
      name: string;
      address: string;
      lawFirmData: string;
    };
  }