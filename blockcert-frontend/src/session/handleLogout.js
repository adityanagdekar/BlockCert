import axios from "axios";

const handleLogout = async (navigate) => {
  try {
    await axios.post(
      "http://localhost:8080/auth/logout",
      {},
      { withCredentials: true }
    );
    localStorage.clear();
    navigate("/");
  } catch (error) {
    console.error("Logout failed", error);
  }
};

export default handleLogout;
