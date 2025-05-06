package com.itj.blockcert.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itj.blockcert.Model.AppUser;
import com.itj.blockcert.Repository.UserRepository;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/users")
    public List<AppUser> getUsers() {
        return userRepo.findAll();
    }
}
