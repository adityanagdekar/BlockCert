import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import "../style/Login.css";
import axios from "axios";

const Login = () => {
  const loginFormRef = useRef(null);
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

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

      const role = response.data.role;
      // Conditional routing
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

  return (
    <div className="Main-Container ">
      <div className="Login-Container">
        <Header />
        <div className="Login-Form-Container">
          <form
            ref={loginFormRef}
            onSubmit={handleLogin}
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
            <button type="submit">Login</button>
            <div className="Login-Message">{message}</div>
          </form>
        </div>
      </div>
    </div>
  );
};
export default Login;
