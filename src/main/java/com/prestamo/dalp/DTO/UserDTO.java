package com.prestamo.dalp.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class UserDTO {
    private Long id;

    @NotNull(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @NotNull(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Long roleId; // Solo necesitamos el ID del rol
}