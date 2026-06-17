export interface SignUpRequest {
  username: string;
  email: string;
  password: string;
}

export interface SignInRequest {
  email: string;
  password: string;
}

export interface AuthStatusResponse {
  email: string;
  authenticated: boolean;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}
