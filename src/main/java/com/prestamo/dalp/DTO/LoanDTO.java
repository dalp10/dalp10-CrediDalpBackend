package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.LoanStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long id;
    private BigDecimal amount;         // Monto original del préstamo (capital)
    private BigDecimal interestRate;   // Tasa de interés original
    private LocalDate issueDate;      // Fecha de emisión del préstamo
    private LocalDate dueDate;        // Fecha de vencimiento (opcional)
    private Long clientId;            // ID del cliente asociado
    private String loanCode;          // Código único del préstamo
    private BigDecimal interestAmount; // Interés original calculado
    private BigDecimal totalAmount;   // Saldo pendiente (se irá recalculando)
    private BigDecimal interestPaid;  // Interés pagado acumulado
    private BigDecimal capitalPaid;   // Capital pagado acumulado
    private BigDecimal remainingCapital; // Capital pendiente
    private BigDecimal remainingInterest; // Interés pendiente
    private LoanStatus status;        // Estado del préstamo
    private Integer daysOverdue;      // Días de mora
}