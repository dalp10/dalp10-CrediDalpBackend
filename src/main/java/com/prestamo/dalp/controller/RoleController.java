package com.prestamo.dalp.controller;

import com.prestamo.dalp.model.Role;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operaciones relacionadas con roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los roles", description = "Devuelve una lista de todos los roles")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida con éxito")
    public ResponseEntity<CustomApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        CustomApiResponse<List<Role>> response = new CustomApiResponse<>(200, "Lista de roles obtenida con éxito", roles, null);
        return ResponseEntity.ok(response);
    }
}