import { signIn } from "../repository/AuthRepository";
import type { SignInRequest } from "../type/auth";

export interface ValidationErrors {
  email?: string;
  password?: string;
}

export async function signInWithValidation(
  data: SignInRequest
): Promise<{ errors: ValidationErrors } | void> {
  const errors: ValidationErrors = {};

  if (!data.email.trim()) {
    errors.email = "Email is required";
  } else if (data.email.length > 320) {
    errors.email = "Email must be at most 320 characters";
  }

  if (!data.password.trim()) {
    errors.password = "Password is required";
  }

  if (errors.email || errors.password) {
    return { errors };
  }

  await signIn(data);
}
