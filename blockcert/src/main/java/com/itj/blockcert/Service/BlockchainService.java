package com.itj.blockcert.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import com.itj.blockcert.Contracts.AcademicCredentialRegistry;
import com.itj.blockcert.Model.Certificate;
import com.itj.blockcert.Repository.CertificateRepository;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockchainService {
    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    @Value("${blockchain.ganache.url}")
    private String ganacheUrl;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    @Value("${blockchain.private.key}")
    private String privateKey;

    private final CertificateRepository certificateRepository;
    private final IPFSService ipfsService;
    
    private Web3j web3j;
    private Credentials credentials;
    private AcademicCredentialRegistry contract;

    @Autowired
    public BlockchainService(CertificateRepository certificateRepository, IPFSService ipfsService) {
        this.certificateRepository = certificateRepository;
        this.ipfsService = ipfsService;
    }

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing blockchain connection to: {}", ganacheUrl);

            // Initialize Web3j connection
            web3j = Web3j.build(new HttpService(ganacheUrl));
            
            // Load credentials from private key
            credentials = Credentials.create(privateKey);
            logger.info("Loaded credentials for address: {}", credentials.getAddress());
            
            // Gas provider for transactions
            ContractGasProvider gasProvider = new DefaultGasProvider();
            
            // Load smart contract
            contract = AcademicCredentialRegistry.load(
                    contractAddress, 
                    web3j, 
                    credentials, 
                    gasProvider
            );
            
            logger.info("Smart contract loaded at address: {}", contractAddress);
        } catch (Exception e) {
            logger.error("Failed to initialize blockchain connection", e);
            throw new RuntimeException("Blockchain initialization failed", e);
        }
    }

    /**
     * Calculate SHA-256 hash of file
     */
    public String calculateFileHash(byte[] fileData) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileData);
        return Numeric.toHexString(hashBytes);
    }

    /**
     * Issue a new credential on blockchain
     */
    public Map<String, String> issueCredential(MultipartFile file, String studentId) throws Exception {
        logger.info("Issuing credential for student: {}", studentId);
        
        // 1. Upload file to IPFS
        String ipfsCID = ipfsService.uploadFile(file);
        logger.info("File uploaded to IPFS with CID: {}", ipfsCID);
        
        // 2. Calculate file hash
        String fileHash = calculateFileHash(file.getBytes());
        logger.info("File hash calculated: {}", fileHash);
        
        // 3. Issue credential on blockchain
        TransactionReceipt receipt = contract.issueCredential(
                studentId,
                ipfsCID,
                fileHash
        ).send();
        
        String transactionHash = receipt.getTransactionHash();
        logger.info("Credential issued on blockchain, tx hash: {}", transactionHash);
        
        // 4. Save to database
        Certificate certificate = new Certificate();
        certificate.setStudentId(studentId);
        certificate.setCidHash(ipfsCID);
        certificate.setFileName(file.getOriginalFilename());
        certificate.setIssueDate(LocalDate.now());
        certificateRepository.save(certificate);
        
        // 5. Return transaction information
        Map<String, String> result = new HashMap<>();
        result.put("transactionHash", transactionHash);
        result.put("ipfsCID", ipfsCID);
        result.put("fileHash", fileHash);
        
        return result;
    }

    /**
     * Verify a credential using blockchain
     */
    public boolean verifyCredential(MultipartFile file, String studentId) {
        try {
            // Calculate file hash
            String fileHash = calculateFileHash(file.getBytes());
            logger.info("Verifying file with hash: {}", fileHash);
            
            // Call the smart contract to verify
            return contract.verifyCredentialByHash(fileHash).send();
        } catch (Exception e) {
            logger.error("Error verifying credential", e);
            return false;
        }
    }
}
