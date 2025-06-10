/**
 * Simple deployment script for AcademicCredentialRegistry using ethers.js
 * No Hardhat required - works directly with Ganache
 */
const fs = require('fs-extra');
const path = require('path');
const solc = require('solc');
const { ethers } = require('ethers');
require('dotenv').config();

// Configuration
const ganacheUrl = process.env.GANACHE_URL || 'http://localhost:7545';
const privateKey = process.env.PRIVATE_KEY;

// Connect to Ganache
const provider = new ethers.providers.JsonRpcProvider(ganacheUrl);
const wallet = new ethers.Wallet(privateKey, provider);

// Helper to compile the contract
async function compileContract() {
  console.log('Compiling contract...');
  
  // Read the contract source
  const contractPath = path.resolve(__dirname, '../contracts/AcademicCredentialRegistry.sol');
  const source = fs.readFileSync(contractPath, 'utf8');
  
  // Prepare compiler input
  const input = {
    language: 'Solidity',
    sources: {
      'AcademicCredentialRegistry.sol': {
        content: source
      }
    },
    settings: {
      outputSelection: {
        '*': {
          '*': ['abi', 'evm.bytecode']
        }
      }
    }
  };
  
  // Compile contract
  const output = JSON.parse(solc.compile(JSON.stringify(input)));
  
  // Get contract data
  const contractData = output.contracts['AcademicCredentialRegistry.sol']['AcademicCredentialRegistry'];
  const abi = contractData.abi;
  const bytecode = contractData.evm.bytecode.object;
  
  return { abi, bytecode };
}

// Deploy the contract
async function deployContract() {
  try {
    // Compile the contract
    const { abi, bytecode } = await compileContract();
    
    // Log deployment status
    console.log('Deploying contract to Ganache...');
    console.log('Network:', ganacheUrl);
    console.log('Deployer address:', wallet.address);
    
    // Create factory and deploy
    const factory = new ethers.ContractFactory(abi, bytecode, wallet);
    const contract = await factory.deploy();
    
    // Wait for deployment
    await contract.deployed();
    
    console.log('Contract deployed successfully!');
    console.log('Contract address:', contract.address);
    
    // Save contract info for later use
    const deploymentInfo = {
      address: contract.address,
      abi: abi,
      deployedAt: new Date().toISOString(),
      deployer: wallet.address
    };
    
    // Create deployments directory if it doesn't exist
    const deploymentsDir = path.resolve(__dirname, '../deployments');
    if (!fs.existsSync(deploymentsDir)) {
      fs.mkdirSync(deploymentsDir);
    }
    
    // Save deployment info
    fs.writeFileSync(
      path.resolve(deploymentsDir, 'deployment.json'),
      JSON.stringify(deploymentInfo, null, 2)
    );
    
    console.log('Deployment info saved to: deployments/deployment.json');
    
    return { contract, abi };
  } catch (error) {
    console.error('Deployment failed:', error);
    process.exit(1);
  }
}

// Test the deployed contract
async function testContract(contract) {
  console.log('\nTesting contract...');
  
  // Check if deployer is admin
  const isAdmin = await contract.admins(wallet.address);
  console.log('Deployer is admin:', isAdmin);
  
  // Example credential data
  const studentId = 'student_12';
  const ipfsCID = 'QmTest12345';
  const fileHash = '0xabcdef1234567890';
  
  // Issue a test credential
  console.log('\nIssuing test credential...');
  const tx = await contract.issueCredential(studentId, ipfsCID, fileHash);
  await tx.wait();
  console.log('Test credential issued!');
  
  // Verify credential
  console.log('\nVerifying credential...');
  const isValid = await contract.verifyCredentialByHash(fileHash);
  console.log('Credential is valid:', isValid);
  
  return true;
}

// Main function
async function main() {
  console.log('Starting deployment process...');
  
  // Deploy contract
  const { contract, abi } = await deployContract();
  
  // Test contract functionality
  await testContract(contract);
  
  console.log('\nDeployment and testing completed successfully!');
}

// Run the deployment
main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
  