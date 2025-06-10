package com.itj.blockcert.Controller;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itj.blockcert.Model.Certificate;
import com.itj.blockcert.Repository.CertificateRepository;
import com.itj.blockcert.Service.CertificateService;
import com.itj.blockcert.Service.IPFSService;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/certificates")
public class CertificateController {

	@Autowired
	private CertificateService certificateService;
	@Autowired
	private IPFSService ipfsService;
	@Autowired
	private CertificateRepository certificateRepository;

	@PostMapping("/upload")
	public ResponseEntity<Map<String, String>> uploadCertificate(
            @RequestParam MultipartFile file, 
            @RequestParam String studentId) {

        try {
            // Use the blockchain service to issue credential
            Map<String, String> result = certificateService.issueCertificate(file, studentId);
            
            // Return blockchain transaction details
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

	@PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCertificate(
            @RequestParam MultipartFile file, 
            @RequestParam String studentId) {
        
        boolean valid = certificateService.verifyCertificate(file, studentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        response.put("message", valid ? "Certificate is valid" : "Certificate is invalid");
        response.put("verificationTime", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

	@GetMapping("/view/student/{studentId}")
    public ResponseEntity<List<Certificate>> viewCertificateByStudentId(@PathVariable String studentId) {
        List<Certificate> certificates = certificateService.getStudentCertificates(studentId);
        return ResponseEntity.ok(certificates);
    }
    
    @GetMapping("/gateway/{cid}")
    public ResponseEntity<String> getIpfsGatewayUrl(@PathVariable String cid) {
        String gatewayUrl = ipfsService.getGatewayUrl(cid);
        return ResponseEntity.ok(gatewayUrl);
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadFromIpfs(@RequestBody Map<String, String> request) {
        String cid = request.get("cid");
        String studentId = request.get("studentId");

        System.out.println("certificate id: "+cid +" & studentId: "+studentId);
        // Verify the CID belongs to the student
        Optional<Certificate> certOpt = certificateRepository.findByCidHashAndStudentId(cid, studentId);
        if (certOpt.isEmpty()) {
            String errMsg = "couldn't find records in DB for certificate id: "+cid +" & studentId: "+studentId;
            System.out.println(errMsg);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", errMsg));
        }

        byte[] fileData = new byte[10];
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(certOpt.get().getFileName())
                    .build());

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);

        // try {
        //     // Fetch file from IPFS
        //     byte[] fileData = ipfsService.downloadFileFromCid(cid);

        //     // Return as a downloadable file
        //     HttpHeaders headers = new HttpHeaders();
        //     headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //     headers.setContentDisposition(ContentDisposition.attachment()
        //             .filename(certOpt.get().getFileName())
        //             .build());

        //     return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        // } catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        // }
    }

}
