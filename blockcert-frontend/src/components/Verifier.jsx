import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";

import axios from "axios";
import Header from "./Header";
import MainContainer from "./MainContainer";
import ContentContainer from "./ContentContainer";
import Button from "./Button";
import useAuthCheck from "../session/useAuthCheck";
import handleLogout from "../session/handleLogout";
import "../style/AdminDashBoard.css";

const Verifier = () => {
    // check session for logged-in users
    useAuthCheck();

  const navigate = useNavigate();
  const formRef = useRef(null);

  const [certificateId, setCertificateId] = useState("");
  const [verificationResult, setVerificationResult] = useState("");

  const handleVerify = async () =>{
    try {
        const cid = certificateId;
        // Try to fetch the document from an IPFS gateway
        const response = await fetch(`https://gateway.pinata.cloud/ipfs/${cid}`);
        
        if (response.ok) {
            console.log("Document exists on IPFS");
            setVerificationResult("✅ Certificate verified successfully!");
            // formRef.current.reset(); // ✅ resets file input too
            // setCertificateId("");
        } else {
            console.log("Document not found or inaccessible");
            setVerificationResult("✅ Certificate verified successfully!");
        }
    } catch (error) {
        console.error("Error verifying document:", error);
        return false;
    }
  }

    return(
        <MainContainer>
            <Header />
            <ContentContainer>

                <h1 className="Dashboard-Title">Welcome to Verifier Dashboard.</h1>
                <div className="File-Header">
                <h2>Enter Certificate Id to verify authenticity</h2>
                <Button text="Log out" onClick={() => handleLogout(navigate)} />
                </div>
                <div className="File-Container">
                <div className="Upload-File-Container">
                    <form ref={formRef} onSubmit={handleVerify}>
                    <div className="Student-Container">
                        <p>Enter Certificate Id</p>
                        <input
                        type="text"
                        value={certificateId}
                        onChange={(e) => setCertificateId(e.target.value)}
                        required
                        />
                    </div>
                    <Button text="Verify" onClick={handleVerify} />
                    </form>
                </div>

                <div className="Verify-File-Container">
                    <p>Verification Result:</p>
                    <div className="Verification-Box">
                        {verificationResult || "No results yet."}
                    </div>
                </div>
                </div>
            </ContentContainer>
        </MainContainer>
    );
}

export default Verifier;
