import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../style/Login.css";
import axios from "axios";
import Header from "./Header";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Button from "./Button";
import useAuthCheck from "../session/useAuthCheck";

const Login = () => {
  // check session for logged-in users
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

      // show the msg from backend
      setMessage(response.data.msg);
      loginFormRef.current.reset();
      setUserName("");
      setPassword("");

      // set the userName/ userId in localStorage
      localStorage.setItem("userName", userName);

      // get the role
      const role = response.data.role;
      // Conditional routing based in role
      if (role === "ADMIN") {
        navigate("/admin");
      } else if (role === "STUDENT") {
        navigate("/student");
      }
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
        <Header />
        <ContentContainer>
          <form
            ref={loginFormRef}
            onSubmit={isRegisterMode ? handleRegister : handleLogin}
            className="Login-Form"
          >
            <h2>Login</h2>
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
                </select>
              </>
            )}
            <Button
              type="submit"
              text={isRegisterMode ? "Register" : "Login"}
            />
            <div className="Login-Message">{message}</div>
            <Button
              type="button"
              onClick={toggleRegisterMode}
              text={isRegisterMode ? "Back to Login" : "New user? Register"}
            />
          </form>
        </ContentContainer>
      </div>
    </MainContainer>
  );
};
export default Login;
