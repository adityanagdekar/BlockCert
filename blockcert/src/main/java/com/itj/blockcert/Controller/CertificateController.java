package com.itj.blockcert.Controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itj.blockcert.Model.Certificate;
import com.itj.blockcert.Repository.CertificateRepository;
import com.itj.blockcert.Service.CertificateService;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

	@Autowired
	private CertificateService certificateService;
	@Autowired
	private CertificateRepository certificateRepository;

	@PostMapping("/upload")
	// upload Certificate
	public ResponseEntity<String> uploadCertificate(@RequestParam MultipartFile file, 
			@RequestParam String studentId) {

		try {
			// Saving file temporarily in ./temp/uploads
			String uploadDir = System.getProperty("user.dir") + "/temp/uploads"; 
			File uploadFolder = new File(uploadDir);
			if (!uploadFolder.exists()) 
				uploadFolder.mkdirs();
			
			System.out.println("Uploading to: " + uploadDir);
			
			String ogFilename = file.getOriginalFilename();
			File targetFile = new File(uploadDir, ogFilename);
			file.transferTo(targetFile);
			
			System.out.println("Received file: " + file.getOriginalFilename());
			System.out.println("Student ID: " + studentId);
			
			// simulating a CID using UUID
			String fakeCid = UUID.randomUUID().toString();
			
			// saving certificate metadata to DB
			Certificate certificate= new Certificate();
			certificate.setCidHash(fakeCid);
			certificate.setFileName(ogFilename);
			certificate.setStudentId(studentId);
			certificate.setIssueDate(LocalDate.now());
			
			certificateRepository.save(certificate);
			return ResponseEntity.ok("File uploaded. Simulated CID: " + fakeCid);
		} catch (IOException e) {
			return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
		}
	}

	@PostMapping("/verify")
	// verify Certificate
	public ResponseEntity<String> verifyCertificate(@RequestParam MultipartFile file, @RequestParam String studentId) {
		boolean valid = certificateService.verifyCertificate(file, studentId);
		return ResponseEntity.ok(valid ? "Verified" : "Invalid");
	}

	@GetMapping("/view/student/{studentId}")
	// view Certificate
	public ResponseEntity<List<Certificate>> viewCertificateByStudentId(@PathVariable String studentId) {
		System.out.println("Student ID: " + studentId);
		List<Certificate> certificates = certificateRepository.findByStudentId(studentId);
		
		for(Certificate certificate: certificates) {
			System.out.println(certificate.toString());
		}
		return ResponseEntity.ok(certificates);
	}
}
