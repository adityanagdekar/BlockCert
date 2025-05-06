import { useState, useRef } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import axios from "axios";

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [studentId, setStudentId] = useState("");
  const [verificationResult, setVerificationResult] = useState("");
  const formRef = useRef(null);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleUpload = async (e) => {
    // if (!selectedFile) return;
    // Placeholder logic
    // setVerificationResult("✅ Certificate verified successfully!");

    e.preventDefault();
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("studentId", studentId);

    try {
      const response = await axios.post(
        "http://localhost:8080/certificates/upload",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setVerificationResult(response.data);
      formRef.current.reset(); // ✅ resets file input too
      setSelectedFile(null);
      setStudentId("");
    } catch (error) {
      console.error(error);
      setVerificationResult("Upload failed");
    }
  };

  return (
    <div className="Main-Container">
      <div className="Header">
        <h1>🎓 BlockCert: Academic Credential Verification</h1>
      </div>
      <div className="Content-Container">
        <h2>Upload your certificate to verify authenticity</h2>

        <div className="File-Container">
          <div className="Upload-File-Container">
            <form ref={formRef} onSubmit={handleUpload}>
              <div className="Student-Container">
                <p>Enter Student Id</p>
                <input
                  type="text"
                  value={studentId}
                  onChange={(e) => setStudentId(e.target.value)}
                  required
                />
              </div>
              <br />
              <p>Select or drag & drop your file</p>
              <input type="file" onChange={handleFileChange} />
              <button>Upload & Verify</button>
            </form>
          </div>

          <div className="Verify-File-Container">
            <p>Verification Result:</p>
            <div className="Verification-Box">
              {verificationResult || "No file uploaded yet."}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
