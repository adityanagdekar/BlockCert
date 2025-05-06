package com.itj.blockcert;

import com.itj.blockcert.Model.Role;
import com.itj.blockcert.Model.AppUser;
import com.itj.blockcert.Repository.RoleRepository;
import com.itj.blockcert.Repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        Role adminRole = new Role(null, "ADMIN");
//        Role studentRole = new Role(null, "STUDENT");
//
//        roleRepository.save(adminRole);
//        roleRepository.save(studentRole);
//
//        userRepository.save(new AppUser(null, "admin_12", 
//        		passwordEncoder.encode("admin_1234"), adminRole));
//        userRepository.save(new AppUser(null, "student_12", 
//        		passwordEncoder.encode("student_1234"), studentRole));
    }
}

