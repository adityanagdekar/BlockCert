package com.itj.blockcert.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itj.blockcert.Model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
