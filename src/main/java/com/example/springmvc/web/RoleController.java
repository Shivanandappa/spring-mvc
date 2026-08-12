package com.example.springmvc.web;

import com.example.springmvc.repository.RoleRepository;
import com.example.springmvc.web.dto.RoleResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> list() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .toList();
    }
}
