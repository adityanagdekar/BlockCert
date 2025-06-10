package com.itj.blockcert.Controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itj.blockcert.DTO.LoginResponse;
import com.itj.blockcert.Model.AppUser;
import com.itj.blockcert.Model.Role;
import com.itj.blockcert.Repository.RoleRepository;
import com.itj.blockcert.Repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(
	        @RequestParam String userName,
	        @RequestParam String role,
	        @RequestParam String password
	) {
	    // Check for existing username
	    if (userRepository.findByUsername(userName) != null) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
	    }
	    // Fetch Role object
	    Role userRole = roleRepository.findByRoleName(role.toUpperCase());
	    if (userRole == null) {
	        return ResponseEntity.badRequest().body("Invalid role");
	    }
	    // Save new user
	    AppUser newUser = new AppUser(null, userName, passwordEncoder.encode(password), userRole);
	    userRepository.save(newUser);

	    return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String userName, @RequestParam String password, HttpSession session) {
		AppUser user = userRepository.findByUsername(userName);

		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			String role = user.getRole().getRoleName();
			String msg = "Welcome " + user.getUsername() + " (" + role + ")";

			// storing in session
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("role", role);

			return ResponseEntity.ok(new LoginResponse(msg, role));
		}
		return ResponseEntity.status(401).body("Invalid credentials");
	}

	@PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // destroy the session
        return ResponseEntity.ok("Logged out successfully");
	}

	@GetMapping("/session")
	public ResponseEntity<?> getSession(HttpSession session) {
	    String userName = (String) session.getAttribute("userName");
	    String role = (String) session.getAttribute("role");
	    if (userName != null) {
	        return ResponseEntity.ok(Map.of("userName", userName, "role", role));
	    } else {
	        return ResponseEntity.status(401).body("Not authenticated");
	    }
	}

}
