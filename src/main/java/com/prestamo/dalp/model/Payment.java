package com.prestamo.dalp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociación al préstamo o crédito. Si Credit es una entidad diferente, puedes cambiar el tipo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // Nuevo campo para almacenar el número de cuota que se está pagando
    @NotNull
    @Column(name = "installment_number")
    private Integer installmentNumber;

    @NotNull
    private BigDecimal capitalPaid;  // Monto pagado del capital

    @NotNull
    private BigDecimal interestPaid;  // Monto pagado del interés

    @NotNull
    private BigDecimal totalPaid;  // Monto total pagado (capital + interés)

    @NotNull
    private LocalDate paymentDate;  // Fecha en que se realizó el pago

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;  // Medio de pago (efectivo, transferencia, Yape, Plin)

    // Método para calcular el total pagado automáticamente
    @PrePersist
    @PreUpdate
    public void calculateTotalPAGADO() {
        this.totalPaid = this.capitalPaid.add(this.interestPaid);
    }
}
