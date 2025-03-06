package com.prestamo.dalp.controller;

import com.prestamo.dalp.model.Role;
import com.prestamo.dalp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")  // Permite solicitudes desde el frontend Angular
@RestController
@RequestMapping("/api/roles")  // Ruta para los roles
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    // Obtener todos los roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
