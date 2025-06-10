// test-connection.js
const { ethers } = require('ethers');

async function testConnection() {
    try {
        console.log("Attempting to connect to Ganache...");
        const provider = new ethers.providers.JsonRpcProvider("http://localhost:8545");
        
        console.log("Waiting for connection...");
        const network = await provider.getNetwork();
        console.log('Successfully connected! Network:', network);
        
        const blockNumber = await provider.getBlockNumber();
        console.log('Current block number:', blockNumber);
        
        const accounts = await provider.listAccounts();
        console.log('Available accounts:', accounts);
        
        console.log("Connection test successful!");
    } catch (error) {
        console.error('Connection failed with error:', error);
    }
}

testConnection();