package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.PaymentLoanDTO;
import com.prestamo.dalp.model.PaymentLoan;

public class PaymentLoanMapper {

    // Convierte la entidad PaymentLoan a PaymentLoanDTO
    public static PaymentLoanDTO toDTO(PaymentLoan entity) {
        PaymentLoanDTO dto = new PaymentLoanDTO();
        dto.setId(entity.getId());
        // Se asume que la entidad Loan ya existe y se obtiene su id
        dto.setLoanId(entity.getLoan() != null ? entity.getLoan().getId() : null);
        dto.setInstallmentNumber(entity.getInstallmentNumber());
        dto.setCapitalPaid(entity.getCapitalPaid());
        dto.setInterestPaid(entity.getInterestPaid());
        dto.setTotalPaid(entity.getTotalPaid());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setPaymentMethod(entity.getPaymentMethod());
        return dto;
    }

    // Convierte un PaymentLoanDTO a la entidad PaymentLoan
    // Nota: La asociación al objeto Loan debe ser establecida en el servicio,
    // ya que normalmente solo se dispone del ID en el DTO.
    public static PaymentLoan toEntity(PaymentLoanDTO dto) {
        PaymentLoan entity = new PaymentLoan();
        // Si es una actualización, se puede asignar el ID
        entity.setId(dto.getId());
        // La asociación al Loan se realiza en el servicio utilizando el ID (dto.getLoanId())
        entity.setInstallmentNumber(dto.getInstallmentNumber());
        entity.setCapitalPaid(dto.getCapitalPaid());
        entity.setInterestPaid(dto.getInterestPaid());
        entity.setTotalPaid(dto.getTotalPaid());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setPaymentMethod(dto.getPaymentMethod());
        return entity;
    }
}
