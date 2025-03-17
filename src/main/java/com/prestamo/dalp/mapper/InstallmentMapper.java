package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.InstallmentDTO;
import com.prestamo.dalp.model.Installment;
import java.util.Objects;

public class InstallmentMapper {

    /**
     * Convierte la entidad Installment a InstallmentDTO.
     *
     * @param entity La entidad Installment a convertir.
     * @return Un objeto InstallmentDTO con los datos de la entidad.
     * @throws IllegalArgumentException Si la entidad es nula.
     */
    public static InstallmentDTO toDTO(Installment entity) {
        // Validar que la entidad no sea nula
        Objects.requireNonNull(entity, "La entidad Installment no puede ser nula");

        // Crear un nuevo DTO y asignar los valores de la entidad
        InstallmentDTO dto = new InstallmentDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setStatus(entity.getStatus());
        dto.setInstallmentNumber(entity.getInstallmentNumber());
        dto.setCapitalAmount(entity.getCapitalAmount());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setCapitalPaid(entity.getCapitalPaid());
        dto.setInterestPaid(entity.getInterestPaid());
        dto.setCapitalRemaining(entity.getCapitalRemaining());
        dto.setInterestRemaining(entity.getInterestRemaining());
        dto.setCreditId(entity.getCredit().getId()); // Asignar el creditId

        return dto;
    }

    /**
     * Convierte un InstallmentDTO a la entidad Installment.
     *
     * @param dto El DTO InstallmentDTO a convertir.
     * @return Una entidad Installment con los datos del DTO.
     * @throws IllegalArgumentException Si el DTO es nulo.
     */
    public static Installment toEntity(InstallmentDTO dto) {
        // Validar que el DTO no sea nulo
        Objects.requireNonNull(dto, "El DTO InstallmentDTO no puede ser nulo");

        // Crear una nueva entidad y asignar los valores del DTO
        Installment entity = new Installment();
        entity.setId(dto.getId());
        entity.setAmount(dto.getAmount());
        entity.setDueDate(dto.getDueDate());
        entity.setStatus(dto.getStatus());
        entity.setInstallmentNumber(dto.getInstallmentNumber());
        entity.setCapitalAmount(dto.getCapitalAmount());
        entity.setInterestAmount(dto.getInterestAmount());
        entity.setCapitalPaid(dto.getCapitalPaid());
        entity.setInterestPaid(dto.getInterestPaid());
        entity.setCapitalRemaining(dto.getCapitalRemaining());
        entity.setInterestRemaining(dto.getInterestRemaining());

        // Nota: El creditId no se asigna aquí porque es una relación y debe manejarse en el servicio.

        return entity;
    }
}