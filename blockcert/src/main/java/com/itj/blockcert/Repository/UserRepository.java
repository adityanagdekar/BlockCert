package com.itj.blockcert.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itj.blockcert.Model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
