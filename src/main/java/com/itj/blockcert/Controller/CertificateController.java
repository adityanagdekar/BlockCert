package com.itj.blockcert.Controller;

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
import com.itj.blockcert.Service.CertificateService;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
	
	@Autowired
    private CertificateService certificateService;
	
	@PostMapping("/upload")
	// upload Certificate
	public ResponseEntity<String> uploadCertificate(@RequestParam MultipartFile file, @RequestParam String studentId) {
		String certificateCIDString = certificateService.uploadCertificate(file, studentId);
		return ResponseEntity.ok(certificateCIDString);
	}

	@PostMapping("/verify")
	// verify Certificate
	public ResponseEntity<String> verifyCertificate(@RequestParam MultipartFile file, @RequestParam String studentId) {
		boolean valid = certificateService.verifyCertificate(file, studentId);
		return ResponseEntity.ok(valid ? "Verified" : "Invalid");
	}

	@GetMapping("/view/{studentId}")
	// view Certificate
	public ResponseEntity<Certificate> viewCertificate(@PathVariable String studentId) {
		return ResponseEntity.ok(certificateService.viewCertificate(studentId));
	}
}
