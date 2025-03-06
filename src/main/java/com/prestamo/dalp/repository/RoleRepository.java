package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);  // Buscar un rol por nombre
}
