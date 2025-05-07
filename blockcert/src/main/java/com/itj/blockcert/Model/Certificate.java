package com.itj.blockcert.Model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Certificate {
	@Id
	@GeneratedValue
	private Long id;

	private String studentId;
	// from IPFS
	private String cidHash;
	private String fileName;
	private LocalDate issueDate;

	public Certificate() {
		// Required by JPA
	}

	public Certificate(Long id, String studentId, String cidHash, String fileName, LocalDate issueDate) {
		this.id = id;
		this.studentId = studentId;
		this.cidHash = cidHash;
		this.fileName = fileName;
		this.issueDate = issueDate;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getCidHash() {
		return cidHash;
	}

	public void setCidHash(String cidHash) {
		this.cidHash = cidHash;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}


	@Override
	public String toString() {
		return "Certificate [id=" + id + ", studentId=" + studentId + ", cidHash=" + cidHash + ", fileName=" + fileName
				+ ", issueDate=" + issueDate + "]";
	}

}