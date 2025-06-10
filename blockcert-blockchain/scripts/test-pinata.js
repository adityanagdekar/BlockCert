/**
 * Simple script to test Pinata IPFS integration
 */
const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

// Pinata API configuration
const pinataApiKey = process.env.PINATA_API_KEY;
const pinataSecretApiKey = process.env.PINATA_SECRET_KEY;

// Check if API keys are set
if (!pinataApiKey || !pinataSecretApiKey) {
  console.error('Pinata API keys not found in .env file');
  process.exit(1);
}

/**
 * Upload a file to IPFS via Pinata
 */
async function uploadToPinata(filePath) {
  if (!fs.existsSync(filePath)) {
    throw new Error(`File not found: ${filePath}`);
  }

  console.log(`Uploading file: ${filePath}`);

  // Create form data
  const formData = new FormData();
  const fileStream = fs.createReadStream(filePath);
  const fileName = path.basename(filePath);
  
  formData.append('file', fileStream, { filename: fileName });

  // Add metadata
  const metadata = JSON.stringify({
    name: fileName,
    keyvalues: {
      type: 'academic_credential',
      createdAt: new Date().toISOString(),
    }
  });
  formData.append('pinataMetadata', metadata);

  try {
    const response = await axios.post(
      'https://api.pinata.cloud/pinning/pinFileToIPFS',
      formData,
      {
        maxBodyLength: Infinity,
        headers: {
          'Content-Type': `multipart/form-data; boundary=${formData._boundary}`,
          'pinata_api_key': pinataApiKey,
          'pinata_secret_api_key': pinataSecretApiKey,
        },
      }
    );

    console.log('File uploaded successfully!');
    console.log('IPFS CID:', response.data.IpfsHash);
    console.log('Gateway URL:', `https://gateway.pinata.cloud/ipfs/${response.data.IpfsHash}`);
    
    return response.data.IpfsHash;
  } catch (error) {
    console.error('Error uploading file to Pinata:');
    if (error.response) {
      console.error(error.response.data);
    } else {
      console.error(error.message);
    }
    throw error;
  }
}

/**
 * Create a test certificate file
 */
function createTestCertificate() {
  const testDir = path.join(__dirname, '../test-files');
  if (!fs.existsSync(testDir)) {
    fs.mkdirSync(testDir);
  }
  
  const testFilePath = path.join(testDir, 'test-certificate.txt');
  
  // Create a simple test certificate
  const certificateContent = `
======================================
        TEST ACADEMIC CERTIFICATE
======================================

Student: John Doe
ID: student_12
Course: Introduction to Java
Grade: A
Date: ${new Date().toISOString().split('T')[0]}

This certificate is verified using blockchain
and IPFS technology.

Issued by: Academic Credential System
======================================
`;

  fs.writeFileSync(testFilePath, certificateContent);
  console.log(`Test certificate created at: ${testFilePath}`);
  
  return testFilePath;
}

/**
 * Main function
 */
async function main() {
  console.log('Testing Pinata IPFS integration...');
  
  // Create test certificate
  const testFilePath = createTestCertificate();
  
  try {
    // Upload to IPFS via Pinata
    const cid = await uploadToPinata(testFilePath);
    
    console.log('\nTest completed successfully!');
    console.log('You can view your file at:', `https://gateway.pinata.cloud/ipfs/${cid}`);
    
    // Return values for potential Java integration
    return {
      success: true,
      cid: cid,
      filePath: testFilePath,
      gatewayUrl: `https://gateway.pinata.cloud/ipfs/${cid}`
    };
  } catch (error) {
    console.error('\nTest failed:', error.message);
    return {
      success: false,
      error: error.message
    };
  }
}

// Run the test
main();