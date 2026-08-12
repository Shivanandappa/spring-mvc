package com.example.springmvc.repository;

import com.example.springmvc.domain.Role;
import com.example.springmvc.domain.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
