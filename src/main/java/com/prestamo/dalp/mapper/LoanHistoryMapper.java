package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.LoanHistoryDTO;
import com.prestamo.dalp.model.LoanHistory;
import com.prestamo.dalp.model.Loan;

public class LoanHistoryMapper {

    // Convierte la entidad LoanHistory a LoanHistoryDTO
    public static LoanHistoryDTO toDTO(LoanHistory entity) {
        LoanHistoryDTO dto = new LoanHistoryDTO();
        dto.setId(entity.getId());
        dto.setLoanId(entity.getLoan() != null ? entity.getLoan().getId() : null);
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setCapitalPaid(entity.getCapitalPaid());
        dto.setInterestPaid(entity.getInterestPaid());
        dto.setAmount(entity.getAmount());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }

    // Convierte un LoanHistoryDTO a la entidad LoanHistory
    // Nota: La asociación al objeto Loan debe ser establecida en el servicio,
    // ya que normalmente solo se dispone del ID en el DTO.
    public static LoanHistory toEntity(LoanHistoryDTO dto) {
        LoanHistory entity = new LoanHistory();
        entity.setId(dto.getId());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setInterestAmount(dto.getInterestAmount());
        entity.setCapitalPaid(dto.getCapitalPaid());
        entity.setInterestPaid(dto.getInterestPaid());
        entity.setAmount(dto.getAmount());
        entity.setTimestamp(dto.getTimestamp());
        // La asociación al Loan se realiza en el servicio utilizando el ID (dto.getLoanId())
        return entity;
    }
}