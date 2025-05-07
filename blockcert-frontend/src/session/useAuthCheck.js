import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const useAuthCheck = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const checkSession = async () => {
      try {
        const res = await axios.get("http://localhost:8080/auth/session");
        localStorage.setItem("userName", res.data.userName);
        localStorage.setItem("role", res.data.role);
      } catch (e) {
        console.error("Session expired. Redirecting to login...");
        localStorage.clear();
        navigate("/");
      }
    };
    checkSession();
  }, []);
};

export default useAuthCheck;
