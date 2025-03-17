package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.LoanDTO;
import com.prestamo.dalp.model.Loan;
import java.util.Objects;

public class LoanMapper {

    /**
     * Convierte la entidad Loan a LoanDTO.
     *
     * @param entity La entidad Loan a convertir.
     * @return Un objeto LoanDTO con los datos de la entidad.
     * @throws IllegalArgumentException Si la entidad es nula.
     */
    public static LoanDTO toDTO(Loan entity) {
        // Validar que la entidad no sea nula
        Objects.requireNonNull(entity, "La entidad Loan no puede ser nula");

        // Crear un nuevo DTO y asignar los valores de la entidad
        LoanDTO dto = new LoanDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setInterestRate(entity.getInterestRate());
        dto.setIssueDate(entity.getIssueDate());
        dto.setDueDate(entity.getDueDate());
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null); // Asignar el ID del cliente
        dto.setLoanCode(entity.getLoanCode());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setInterestPaid(entity.getInterestPaid());
        dto.setCapitalPaid(entity.getCapitalPaid());
        dto.setRemainingCapital(entity.getRemainingCapital());
        dto.setRemainingInterest(entity.getRemainingInterest());
        dto.setStatus(entity.getStatus());
        dto.setDaysOverdue(entity.getDaysOverdue());

        return dto;
    }

    /**
     * Convierte un LoanDTO a la entidad Loan.
     *
     * @param dto El DTO LoanDTO a convertir.
     * @return Una entidad Loan con los datos del DTO.
     * @throws IllegalArgumentException Si el DTO es nulo.
     */
    public static Loan toEntity(LoanDTO dto) {
        // Validar que el DTO no sea nulo
        Objects.requireNonNull(dto, "El DTO LoanDTO no puede ser nulo");

        // Crear una nueva entidad y asignar los valores del DTO
        Loan entity = new Loan();
        entity.setId(dto.getId());
        entity.setAmount(dto.getAmount());
        entity.setInterestRate(dto.getInterestRate());
        entity.setIssueDate(dto.getIssueDate());
        entity.setDueDate(dto.getDueDate());
        entity.setLoanCode(dto.getLoanCode());
        entity.setInterestAmount(dto.getInterestAmount());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setInterestPaid(dto.getInterestPaid());
        entity.setCapitalPaid(dto.getCapitalPaid());
        entity.setRemainingCapital(dto.getRemainingCapital());
        entity.setRemainingInterest(dto.getRemainingInterest());
        entity.setStatus(dto.getStatus());
        entity.setDaysOverdue(dto.getDaysOverdue());

        // Nota: El clientId no se asigna aquí porque es una relación y debe manejarse en el servicio.

        return entity;
    }
}