package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.PaymentCreditDTO;
import com.prestamo.dalp.model.PaymentCredit;
import java.util.Objects;

public class PaymentCreditMapper {

    /**
     * Convierte la entidad PaymentCredit a PaymentCreditDTO.
     *
     * @param entity La entidad PaymentCredit a convertir.
     * @return Un objeto PaymentCreditDTO con los datos de la entidad.
     * @throws IllegalArgumentException Si la entidad es nula.
     */
    public static PaymentCreditDTO toDTO(PaymentCredit entity) {
        // Validar que la entidad no sea nula
        Objects.requireNonNull(entity, "La entidad PaymentCredit no puede ser nula");

        // Crear un nuevo DTO y asignar los valores de la entidad
        PaymentCreditDTO dto = new PaymentCreditDTO();
        dto.setId(entity.getId());
        dto.setCreditId(entity.getCredit() != null ? entity.getCredit().getId() : null);
        dto.setInstallmentId(entity.getInstallment() != null ? entity.getInstallment().getId() : null);
        dto.setInstallmentNumber(entity.getInstallmentNumber());
        dto.setCapitalPaid(entity.getCapitalPaid());
        dto.setInterestPaid(entity.getInterestPaid());
        dto.setTotalPaid(entity.getTotalPaid());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setPaymentMethod(entity.getPaymentMethod());

        return dto;
    }

    /**
     * Convierte un PaymentCreditDTO a la entidad PaymentCredit.
     *
     * @param dto El DTO PaymentCreditDTO a convertir.
     * @return Una entidad PaymentCredit con los datos del DTO.
     * @throws IllegalArgumentException Si el DTO es nulo.
     */
    public static PaymentCredit toEntity(PaymentCreditDTO dto) {
        // Validar que el DTO no sea nulo
        Objects.requireNonNull(dto, "El DTO PaymentCreditDTO no puede ser nulo");

        // Crear una nueva entidad y asignar los valores del DTO
        PaymentCredit entity = new PaymentCredit();
        entity.setId(dto.getId());
        entity.setInstallmentNumber(dto.getInstallmentNumber());
        entity.setCapitalPaid(dto.getCapitalPaid());
        entity.setInterestPaid(dto.getInterestPaid());
        entity.setTotalPaid(dto.getTotalPaid());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setPaymentMethod(dto.getPaymentMethod());

        return entity;
    }
}