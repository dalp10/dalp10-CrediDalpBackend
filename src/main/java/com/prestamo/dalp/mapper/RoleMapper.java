package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.RoleDTO;
import com.prestamo.dalp.model.Role;

public class RoleMapper {

    // Convierte la entidad Role a RoleDTO
    public static RoleDTO toDTO(Role entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    // Convierte un RoleDTO a la entidad Role
    public static Role toEntity(RoleDTO dto) {
        Role entity = new Role();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}