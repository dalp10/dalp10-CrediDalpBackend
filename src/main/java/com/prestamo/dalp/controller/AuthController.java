package com.prestamo.dalp.controller;

import com.prestamo.dalp.config.JwtUtil;
import com.prestamo.dalp.model.User;
import com.prestamo.dalp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new RuntimeException("El usuario ya existe"); // 🔹 Evita duplicados
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // 🔹 Hashear la contraseña
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado con éxito");
        return response;
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "El usuario no existe", "error", "USER_NOT_FOUND"));
        }

        if (!passwordEncoder.matches(user.getPassword(), foundUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Contraseña incorrecta", "error", "INVALID_PASSWORD"));
        }

        // Obtener el rol del usuario
        String role = foundUser.get().getRole().getName(); // 🔹 Obtiene el rol desde la base de datos

        // Generar el token con el rol incluido
        String token = jwtUtil.generateToken(user.getUsername(), role);

        // Preparar la respuesta con el token y el rol
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login exitoso");
        response.put("token", token);
        response.put("role", role); // 🔹 Agregar el rol en la respuesta

        return ResponseEntity.ok(response);
    }

}

