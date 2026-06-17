import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { signInWithValidation } from "../../business/service/AuthService";
import "../style/SignIn.css";

export default function SignIn() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{ email?: string; password?: string }>(
    {},
  );
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});

    const result = await signInWithValidation({ email, password });
    if (result) {
      setErrors(result.errors);
      setLoading(false);
      return;
    }

    navigate("/");
  };

  return (
    <form className="signin-wrapper" onSubmit={handleSubmit}>
      <div className="signin-card">
        <h2>Sign In to Nix Docs</h2>

        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
          />
          {errors.email && <span className="error">{errors.email}</span>}
        </label>

        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
          {errors.password && <span className="error">{errors.password}</span>}
        </label>

        <button className="signin-btn" disabled={loading}>
          {loading ? "Signing in..." : "Sign In"}
        </button>

        <div className="signin-divider">or</div>

        <a
          href="https://github.com/login/oauth/authorize"
          className="github-btn"
        >
          Sign in with GitHub
        </a>

        <p className="signin-footer">
          You don't have an account? <NavLink to="/signup">Sign up</NavLink>
        </p>
      </div>
    </form>
  );
}
