package com.itj.blockcert.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itj.blockcert.Model.Certificate;


@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>  {
    // Certificate Repo
	List<Certificate> findByStudentId(String studentId);
}