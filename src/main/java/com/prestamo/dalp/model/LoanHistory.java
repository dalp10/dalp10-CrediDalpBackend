package com.prestamo.dalp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LoanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el préstamo al que pertenece este historial.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // Campos que se desean registrar en el historial
    @Column(nullable = false)
    private BigDecimal totalAmount;  // Deuda total (capital + interés)

    @Column(nullable = false)
    private BigDecimal interestAmount;  // Monto de interés calculado

    @Column(nullable = false)
    private BigDecimal capitalPaid;     // Capital pagado hasta el momento

    @Column(nullable = false)
    private BigDecimal interestPaid;    // Interés pagado hasta el momento

    @Column(nullable = false)
    private BigDecimal amount;  // Monto del préstamo

    // Fecha y hora en que se registró este snapshot
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
