package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 🔹 Buscar usuario por username
    // Método para verificar si el nombre de usuario existe en la base de datos
    boolean existsByUsername(String username);  // Este método verifica si el nombre de usuario ya está registrado
}

