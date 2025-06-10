import { QRCodeSVG } from 'qrcode.react';
import "../style/QRCode.css";

const QRDisplay = (props) => {
    const cid = props.cid;
  const ipfsUrl = `⁠ https://gateway.pinata.cloud/ipfs/${cid}`;

  return(
    <div style={{ textAlign: "center" }}>
      <p><strong>Scan to verify this certificate:</strong></p>
      <div className='QR-Container'>
        <QRCodeSVG value={ipfsUrl} size={100} />
      </div>
    </div>
  );
}

export default QRDisplay;