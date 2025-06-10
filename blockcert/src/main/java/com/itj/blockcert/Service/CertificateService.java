package com.itj.blockcert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itj.blockcert.Model.Certificate;
import com.itj.blockcert.Repository.CertificateRepository;

import java.util.List;
import java.util.Map;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired
    private IPFSService ipfsService;
    
    /**
     * Issue a new certificate
     */
    public Map<String, String> issueCertificate(MultipartFile file, String studentId) throws Exception {
        // This will upload to IPFS, store on blockchain, and save to DB
        return blockchainService.issueCredential(file, studentId);
    }
    
    /**
     * Verify a certificate
     */
    public boolean verifyCertificate(MultipartFile file, String studentId) {
        // This will verify the certificate on the blockchain
        return blockchainService.verifyCredential(file, studentId);
    }
    
    /**
     * Get student certificates with gateway URLs
     */
    public List<Certificate> getStudentCertificates(String studentId) {
        List<Certificate> certificates = certificateRepository.findByStudentId(studentId);
        
        // Add gateway URLs for viewing
        certificates.forEach(cert -> {
            String viewUrl = ipfsService.getGatewayUrl(cert.getCidHash());
            // We don't have a viewUrl field in the Certificate entity, 
            // but you could add one or handle this in the controller
        });
        
        return certificates;
    }
}