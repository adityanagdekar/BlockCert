import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "../style/Login.css";
import axios from "axios";
import Header from "./Header";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Button from "./Button";
import useAuthCheck from "../session/useAuthCheck";

import loginImage from "../assets/blockcertlogin.png";

const Login = () => {
  useAuthCheck();

  const loginFormRef = useRef(null);
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("STUDENT");
  const [message, setMessage] = useState("");
  const [isRegisterMode, setIsRegisterMode] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    const formData = new URLSearchParams();
    formData.append("userName", userName);
    formData.append("password", password);

    try {
      const response = await axios.post(
        "http://localhost:8080/auth/login",
        formData,
        {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        }
      );

      setMessage(response.data.msg);
      loginFormRef.current.reset();
      setUserName("");
      setPassword("");

      localStorage.setItem("userName", userName);

      const role = response.data.role;
      if (role === "ADMIN") navigate("/admin");
      else if (role === "STUDENT") navigate("/student");
      else if (role === "VERIFIER") navigate("/verifier");
    } catch (error) {
      console.error(error);
      setMessage("Login failed");
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    const formData = new URLSearchParams();
    formData.append("userName", userName);
    formData.append("password", password);
    formData.append("role", role);

    try {
      const response = await axios.post(
        "http://localhost:8080/auth/register",
        formData,
        {
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          withCredentials: true,
        }
      );

      setMessage(response.data);
      setIsRegisterMode(false);
      loginFormRef.current.reset();
      setUserName("");
      setPassword("");
    } catch (error) {
      console.error(error);
      setMessage("Registration failed");
    }
  };

  const toggleRegisterMode = () => {
    setIsRegisterMode(!isRegisterMode);
    setMessage("");
  };

  return (
    <MainContainer>
      <div className="Login-Container">
        <h1 className="Login-Heading">
          🎓 BlockCert: Academic Credential Verification
        </h1>

        <div className="Login-Content-Split">
          <div className="Login-Left">
            <img
              src={loginImage}
              alt="Student illustration"
              className="Login-Image"
            />
          </div>

          <div className="Login-Right">
            <form
              ref={loginFormRef}
              onSubmit={isRegisterMode ? handleRegister : handleLogin}
              className="Login-Form"
            >
              <div className="Login-Welcome">
                <div>Hello,</div>
                <div>Welcome Back</div>
              </div>

              <label>Username</label>
              <input
                type="text"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
              />

              <label>Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              {isRegisterMode && (
                <>
                  <label>Role</label>
                  <select
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                    required
                  >
                    <option value="STUDENT">Student</option>
                    <option value="ADMIN">Admin</option>
                    <option value="VERIFIER">Verifier</option>
                  </select>
                </>
              )}

              <div className="Login-ButtonGroup">
                <Button
                  type="submit"
                  text={isRegisterMode ? "Register" : "Login"}
                />

                <div className="Login-Or">or</div>

                <Button
                  type="button"
                  onClick={toggleRegisterMode}
                  text={isRegisterMode ? "Back to Login" : "New user? Register"}
                />
              </div>

              <div className="Login-Message">{message}</div>
            </form>
          </div>
        </div>
      </div>
    </MainContainer>
  );
};

export default Login;
