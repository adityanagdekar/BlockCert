package com.itj.blockcert.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itj.blockcert.Model.Certificate;
import com.itj.blockcert.Repository.CertificateRepository;

@Service
public class CertificateService {

	@Autowired
	private CertificateRepository repo;

	// upload Certificate
	public String uploadCertificate(MultipartFile file, String studentId) {
		return "";
	}

	// verify Certificate
	public boolean verifyCertificate(MultipartFile file, String studentId) {
		return false;
	}

	// view Certificate
	public Certificate viewCertificate(String studentId) {
		return null;
	}
}
