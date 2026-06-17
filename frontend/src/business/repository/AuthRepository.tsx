import { apiBaseUrl } from "../../config";
import type { SignUpRequest, SignInRequest, AuthStatusResponse, ApiError } from "../type/auth";

export async function signUp(data: SignUpRequest): Promise<void> {
  const response = await fetch(`${apiBaseUrl}/api/auth/signup`, {
    method: "POST",
    headers: {
      "X-API-Version": "1",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error: ApiError = await response.json();
    throw error;
  }
}

export async function signIn(data: SignInRequest): Promise<void> {
  const response = await fetch(`${apiBaseUrl}/api/auth/signin`, {
    method: "POST",
    headers: {
      "X-API-Version": "1",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error: ApiError = await response.json();
    throw error;
  }
}

export async function status(): Promise<AuthStatusResponse> {
  const response = await fetch(`${apiBaseUrl}/api/auth/status`, {
    headers: {
      "X-API-Version": "1",
    },
    credentials: "include",
  });

  if (!response.ok) {
    const error: ApiError = await response.json();
    throw error;
  }

  return response.json();
}

export async function refresh(): Promise<void> {
  const response = await fetch(`${apiBaseUrl}/api/auth/refresh`, {
    method: "POST",
    headers: {
      "X-API-Version": "1",
    },
    credentials: "include",
  });

  if (!response.ok) {
    const error: ApiError = await response.json();
    throw error;
  }
}
