package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.ClientDTO;
import com.prestamo.dalp.model.Client;

public class ClientMapper {

    // Convierte la entidad Client a ClientDTO
    public static ClientDTO toDTO(Client entity, boolean hasCredits) {
        ClientDTO dto = new ClientDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setDocumentNumber(entity.getDocumentNumber());
        dto.setClientIdentifier(entity.getClientIdentifier()); // Mapea el clientIdentifier
        dto.setHasCredits(hasCredits); // Mapea el booleano hasCredits
        return dto;
    }

    // Convierte un ClientDTO a la entidad Client
    public static Client toEntity(ClientDTO dto) {
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setDocumentNumber(dto.getDocumentNumber());
        entity.setClientIdentifier(dto.getClientIdentifier()); // Mapea el clientIdentifier
        // El campo hasCredits no se mapea a la entidad, ya que es un campo calculado o derivado
        return entity;
    }

    // Constructor alternativo para crear un ClientDTO directamente desde un Client y un booleano hasCredits
    public static ClientDTO toDTO(Client entity) {
        return new ClientDTO(entity, false); // Por defecto, hasCredits es false
    }
}
