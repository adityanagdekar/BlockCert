import "../style/App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Login";
import AdminDashBoard from "./AdminDashBoard";
import StudentDashBoard from "./StudentDashBoard";
import MainContainer from "./MainContainer";
import Verifier from "./Verifier";
import useAuthCheck from "../session/useAuthCheck";
import handleLogout from "../session/handleLogout";

function App() {
  return (
    <MainContainer>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/admin" element={<AdminDashBoard />} />
          <Route path="/student" element={<StudentDashBoard />} />
          <Route path="/verifier" element={<Verifier />} />
        </Routes>
      </BrowserRouter>
    </MainContainer>
  );
}

export default App;
