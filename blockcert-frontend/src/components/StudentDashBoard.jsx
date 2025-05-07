import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Header from "./Header";
import Button from "./Button";
import "../style/StudentDashboard.css";
import useAuthCheck from "../session/useAuthCheck";
import handleLogout from "../session/handleLogout";

const Student = () => {
  // check session for logged-in users
  useAuthCheck();

  const navigate = useNavigate();
  const [certificates, setCertificates] = useState([]);
  // stored during login
  const studentUserName = localStorage.getItem("userName");

  useEffect(() => {
    const fetchCertificates = async () => {
      const formData = new URLSearchParams();
      formData.append("userName", studentUserName);

      try {
        const res = await axios.get(
          `http://localhost:8080/certificates/view/student/${studentUserName}`
        );
        setCertificates(res.data);
      } catch (error) {
        console.error("Failed to fetch certificates", error);
      }
    };

    if (studentUserName) fetchCertificates();
  }, [studentUserName]);

  return (
    <MainContainer>
      <Header />
      <ContentContainer>
        <div className="Student-Header">
          <h2>
            Hello, these are Certificates for Student ID: {studentUserName}
          </h2>
          <Button text="Log out" onClick={() => handleLogout(navigate)} />
        </div>
        <div className="Student-Container">
          <ul>
            {certificates.map((cert) => (
              <li key={cert.id}>
                <strong>{cert.fileName}</strong> | Issued on: {cert.issueDate}{" "}
                <br />
                CID: <code>{cert.cidHash}</code>
              </li>
            ))}
          </ul>
        </div>
      </ContentContainer>
    </MainContainer>
  );
};
export default Student;
