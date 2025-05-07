import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "../style/Login.css";
import axios from "axios";
import Header from "./Header";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Button from "./Button";

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

  return (
    <MainContainer>
      <div className="Login-Container">
        <Header />
        <ContentContainer>
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
            <Button type="submit" text="Login" />
            <div className="Login-Message">{message}</div>
          </form>
        </ContentContainer>
      </div>
    </MainContainer>
  );
};
export default Login;
