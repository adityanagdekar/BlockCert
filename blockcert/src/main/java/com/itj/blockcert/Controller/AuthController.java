package com.itj.blockcert.Controller;

import org.antlr.v4.runtime.misc.TestRig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itj.blockcert.DTO.LoginResponse;
import com.itj.blockcert.Model.AppUser;
import com.itj.blockcert.Repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String userName, @RequestParam String password){
		AppUser user= userRepository.findByUsername(userName);
		
		if (user != null && 
				passwordEncoder.matches(password, user.getPassword())) {
			String role = user.getRole().getRoleName();
			String msg = "Welcome " + user.getUsername() + " (" + role + ")";
			return ResponseEntity.ok(new LoginResponse(msg, role));
		}
		return ResponseEntity.status(401).body("Invalid credentials");
	}

}
