import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import QRDisplay from "./QRCode";
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
  const studentUserName = localStorage.getItem("userName");
  const [certificateId, setCertificateId] = useState("");

  useEffect(() => {
    const fetchCertificates = async () => {
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

  const handleFileOnClick = async (cid, studentId, fileName) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/certificates/download/`,
        {cid, studentId},
        {
          responseType: "blob",
        }
      );

      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Download failed", err);
    }
  };

  const downLoadFile_old = () =>{
    const formData = new FormData();
    formData.append("file", file);

    axios.post("https://api.pinata.cloud/pinning/pinFileToIPFS", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        "pinata_api_key": "<your_key>",
        "pinata_secret_api_key": "<your_secret>",
      }
    }).then(res => {
      console.log(res.data.IpfsHash); // <-- This is your CID
    });
  }

const handleDirectDownload = (cid, originalFileName) => {
  const url = `https://gateway.pinata.cloud/ipfs/${cid}`;

  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", originalFileName); // hint for browsers
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};


const downloadFile = (cid) => {
  const url = `https://gateway.pinata.cloud/ipfs/${cid}`;
  window.open(url, '_blank', 'noopener,noreferrer');
};

  return (
    <MainContainer>
      <Header />
      <ContentContainer>
          <h1 className="Dashboard-Title">Welcome to Student Dashboard.</h1>
        <div className="Student-Header">
          <h2>
            Hello, these are Certificates for Student ID: {studentUserName}
          </h2>
          <Button text="Log out" onClick={() => handleLogout(navigate)} />
        </div>
        <div className="Student-Container">
          {certificates.map((cert) => (
            <div className="Certificate-Card" key={cert.id}>
              <h3>{cert.fileName}</h3>
              <p><strong>Issued:</strong> {cert.issueDate}</p>
              <p>
                <strong>File ID:</strong><br />
                <code>{cert.cidHash}</code>
              </p>

              <div className="QR-Container">
                {cert.cidHash && <QRDisplay cid={cert.cidHash} />}
              </div>
              
              <div className="card-footer">
                <button
                  className="Download-Button"
                  onClick={() => downloadFile(cert.cidHash)}
                >
                  Download
                </button>
              </div>
            </div>
          ))}
        </div>
      </ContentContainer>
    </MainContainer>
  );
};

export default Student;