import "../style/App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Login";
import AdminDashBoard from "./AdminDashBoard";
import StudentDashBoard from "./StudentDashBoard";
import MainContainer from "./MainContainer";

function App() {
  return (
    <MainContainer>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/admin" element={<AdminDashBoard />} />
          <Route path="/student" element={<StudentDashBoard />} />
        </Routes>
      </BrowserRouter>
    </MainContainer>
  );
}

export default App;
