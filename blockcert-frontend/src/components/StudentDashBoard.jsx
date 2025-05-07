import { useState, useEffect } from "react";
import axios from "axios";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Header from "./Header";

const Student = () => {
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
        <div className="Student-Container">
          <h2>
            Hello, these are Certificates for Student ID: {studentUserName}
          </h2>
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
