package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.LoanStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data   // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los parámetros


public class LoanDTO {
    private Long id;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String loanCode;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private LoanStatus status;
    private Long clientId;
    // Nuevos campos para saldo pendiente
    private BigDecimal remainingCapital;   // Capital pendiente
    private BigDecimal remainingInterest;  // Interés pendiente
    // Getters y Setters
}