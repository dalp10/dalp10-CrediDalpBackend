package com.prestamo.dalp.controller;

import com.prestamo.dalp.model.User;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito")
    public ResponseEntity<CustomApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        CustomApiResponse<List<User>> response = new CustomApiResponse<>(200, "Lista de usuarios obtenida con éxito", users, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Obtiene los detalles de un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<CustomApiResponse<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            CustomApiResponse<User> response = new CustomApiResponse<>(200, "Usuario encontrado", user.get(), null);
            return ResponseEntity.ok(response);
        } else {
            CustomApiResponse<User> response = new CustomApiResponse<>(404, "Usuario no encontrado", null, List.of(new ErrorDetail("userId", "Usuario no encontrado")));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<User>> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            CustomApiResponse<User> response = new CustomApiResponse<>(201, "Usuario creado con éxito", savedUser, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            CustomApiResponse<User> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("user", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            CustomApiResponse<User> response = new CustomApiResponse<>(200, "Usuario actualizado con éxito", user, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<User> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("userId", "Usuario no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<String>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            CustomApiResponse<String> response = new CustomApiResponse<>(200, "Usuario eliminado con éxito", null, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<String> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("userId", "Usuario no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si el nombre de usuario existe", description = "Verifica si un nombre de usuario ya está registrado")
    @ApiResponse(responseCode = "200", description = "Verificación completada")
    public ResponseEntity<CustomApiResponse<Boolean>> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.usernameExists(username);
        CustomApiResponse<Boolean> response = new CustomApiResponse<>(200, "Verificación completada", exists, null);
        return ResponseEntity.ok(response);
    }
}