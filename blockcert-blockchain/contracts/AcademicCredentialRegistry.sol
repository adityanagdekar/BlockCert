// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title AcademicCredentialRegistry
 * @dev Smart contract for storing and verifying academic credentials
 */
contract AcademicCredentialRegistry {
    
    // Credential structure
    struct Credential {
        string ipfsCID;         // IPFS Content Identifier
        string fileHash;        // SHA-256 hash of the document
        address issuer;         // Admin address that issued credential
        uint256 issuedAt;       // Timestamp of issuance
        string studentId;       // Student identifier
    }
    
    // Mapping from credential ID to Credential
    mapping(bytes32 => Credential) public credentials;
    
    // Mapping from student ID to their credentials
    mapping(string => bytes32[]) public studentCredentials;
    
    // Mapping from fileHash to credential ID for verification
    mapping(string => bytes32) public fileHashToCredential;
    
    // Admin addresses
    mapping(address => bool) public admins;
    
    // Events
    event CredentialIssued(bytes32 indexed credentialId, string studentId, string ipfsCID);
    event AdminAdded(address indexed admin);
    event AdminRemoved(address indexed admin);
    
    // Constructor - make deployer an admin
    constructor() {
        admins[msg.sender] = true;
        emit AdminAdded(msg.sender);
    }
    
    // Modifier to restrict to admins
    modifier onlyAdmin() {
        require(admins[msg.sender], "Not authorized: admin role required");
        _;
    }
    
    /**
     * @dev Add a new admin
     * @param admin Address of the new admin
     */
    function addAdmin(address admin) external onlyAdmin {
        require(admin != address(0), "Invalid address");
        admins[admin] = true;
        emit AdminAdded(admin);
    }
    
    /**
     * @dev Remove an admin
     * @param admin Address of the admin to remove
     */
    function removeAdmin(address admin) external onlyAdmin {
        require(admin != msg.sender, "Cannot remove self as admin");
        require(admins[admin], "Address is not an admin");
        admins[admin] = false;
        emit AdminRemoved(admin);
    }
    
    /**
     * @dev Issue a new credential
     * @param studentId Student identifier
     * @param ipfsCID IPFS Content Identifier
     * @param fileHash SHA-256 hash of the document
     * @return credentialId Unique identifier for the credential
     */
    function issueCredential(
        string calldata studentId,
        string calldata ipfsCID,
        string calldata fileHash
    ) external onlyAdmin returns (bytes32) {
        // Generate credential ID using studentId, ipfsCID, and current time for uniqueness
        bytes32 credentialId = keccak256(abi.encodePacked(studentId, ipfsCID, block.timestamp));
        
        // Store credential
        credentials[credentialId] = Credential({
            ipfsCID: ipfsCID,
            fileHash: fileHash,
            issuer: msg.sender,
            issuedAt: block.timestamp,
            studentId: studentId
        });
        
        // Associate credential with student
        studentCredentials[studentId].push(credentialId);
        
        // Store fileHash to credential mapping for verification
        fileHashToCredential[fileHash] = credentialId;
        
        // Emit event
        emit CredentialIssued(credentialId, studentId, ipfsCID);
        
        return credentialId;
    }
    
    /**
     * @dev Get all credentials for a student
     * @param studentId Student identifier
     * @return Array of credential IDs
     */
    function getStudentCredentials(string calldata studentId) 
        external 
        view 
        returns (bytes32[] memory) {
        return studentCredentials[studentId];
    }
    
   
    function getCredential(bytes32 credentialId) 
        external 
        view 
        returns (
            string memory ipfsCID,
            string memory fileHash,
            address issuer,
            uint256 issuedAt,
            string memory studentId
        ) {
        Credential memory cred = credentials[credentialId];
        
        return (
            cred.ipfsCID,
            cred.fileHash,
            cred.issuer,
            cred.issuedAt,
            cred.studentId
        );
    }
    
    /**
     * @dev Verify if a credential is valid by its file hash
     * @param fileHash SHA-256 hash of the document
     * @return bool True if valid, false otherwise
     */
    function verifyCredentialByHash(string calldata fileHash) 
        external 
        view 
        returns (bool) {
        
        bytes32 credentialId = fileHashToCredential[fileHash];
        
        // If no credential found with this hash
        if (credentialId == bytes32(0)) {
            return false;
        }
        
        // Check if this credential exists
        return credentials[credentialId].issuer != address(0);
    }
    
    /**
     * @dev Verify if a credential is valid by CID and file hash combination
     * @param ipfsCID IPFS Content Identifier
     * @param fileHash SHA-256 hash of the document
     * @return bool True if valid, false otherwise
     */
    function verifyCertificate(string calldata ipfsCID, string calldata fileHash) 
        external 
        view 
        returns (bool) {
        
        bytes32 credentialId = fileHashToCredential[fileHash];
        
        // If no credential found with this hash
        if (credentialId == bytes32(0)) {
            return false;
        }
        
        // Verify CID matches
        return keccak256(abi.encodePacked(credentials[credentialId].ipfsCID)) == 
               keccak256(abi.encodePacked(ipfsCID));
    }
}