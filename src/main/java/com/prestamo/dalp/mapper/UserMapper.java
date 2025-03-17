package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.UserDTO;
import com.prestamo.dalp.model.User;
import com.prestamo.dalp.model.Role;

public class UserMapper {

    // Convierte la entidad User a UserDTO
    public static UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setRoleId(entity.getRole() != null ? entity.getRole().getId() : null);
        return dto;
    }

    // Convierte un UserDTO a la entidad User
    // Nota: La asociación al objeto Role debe ser establecida en el servicio,
    // ya que normalmente solo se dispone del ID en el DTO.
    public static User toEntity(UserDTO dto) {
        User entity = new User();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        // La asociación al Role se realiza en el servicio utilizando el ID (dto.getRoleId())
        return entity;
    }
}