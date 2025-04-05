package com.prestamo.dalp.config;

import com.prestamo.dalp.model.Role;
import com.prestamo.dalp.model.User;
import com.prestamo.dalp.repository.RoleRepository;
import com.prestamo.dalp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Solo si usas encoding
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    // Solo si usas Spring Security
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Crear rol ADMIN si no existe
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);
                });

        // Crear usuario admin si no existe
        if (!userRepository.existsByUsername("ARLO")) {
            User admin = new User();
            admin.setUsername("ARLO");

            // Encriptar si tienes PasswordEncoder, si no, usa directamente el texto
            String rawPassword = "admin123";
            if (passwordEncoder != null) {
                admin.setPassword(passwordEncoder.encode(rawPassword));
            } else {
                admin.setPassword(rawPassword);
            }

            admin.setRole(adminRole);
            userRepository.save(admin);
            System.out.println("✅ Usuario por defecto creado: admin / " + rawPassword);
        }
    }
}
