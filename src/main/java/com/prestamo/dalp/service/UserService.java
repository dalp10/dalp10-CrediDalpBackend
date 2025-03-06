package com.prestamo.dalp.service;

import com.prestamo.dalp.model.User;
import com.prestamo.dalp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener un usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Crear un nuevo usuario
    @Transactional
    public User createUser(User user) {
        // Validación: Verificar si el nombre de usuario ya existe
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        // Validación: Verificar que el rol no sea nulo
        if (user.getRole() == null) {
            throw new RuntimeException("El rol del usuario es obligatorio.");
        }

        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }

    // Modificar un usuario existente
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        // Buscar el usuario existente
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Validación: Verificar si el nuevo nombre de usuario ya existe (si ha cambiado)
        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        // Validación: Verificar que el rol no sea nulo
        if (updatedUser.getRole() == null) {
            throw new RuntimeException("El rol del usuario es obligatorio.");
        }

        // Actualizar los datos del usuario
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setRole(updatedUser.getRole());

        // Guardar los cambios
        return userRepository.save(existingUser);
    }

    // Eliminar un usuario
    @Transactional
    public void deleteUser(Long id) {
        // Verificar si el usuario existe
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        // Eliminar el usuario
        userRepository.deleteById(id);
    }

    // Verificar si el nombre de usuario existe
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
