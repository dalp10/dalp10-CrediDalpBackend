package com.prestamo.dalp.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanHistoryDTO {
    private Long id;
    private Long loanId; // ID del préstamo asociado
    private BigDecimal totalAmount;
    private BigDecimal interestAmount;
    private BigDecimal capitalPaid;
    private BigDecimal interestPaid;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}