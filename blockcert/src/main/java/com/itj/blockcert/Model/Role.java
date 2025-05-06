package com.itj.blockcert.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String roleName; // e.g., "ADMIN", "STUDENT"
	
	public Role() {
		// Required by JPA
	}

	public Role(Long id, String roleName) {
		this.id = id;
		this.roleName = roleName;
	}
}
