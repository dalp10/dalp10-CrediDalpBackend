package com.prestamo.dalp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_loans")
@Data
@NoArgsConstructor
public class PaymentLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociación al préstamo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // Número de cuota que se está pagando
    @NotNull
    @Column(name = "installment_number")
    private Integer installmentNumber;

    @NotNull
    private BigDecimal capitalPaid;

    @NotNull
    private BigDecimal interestPaid;

    @NotNull
    private BigDecimal totalPaid;

    @NotNull
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    @PrePersist
    @PreUpdate
    public void calculateTotalPAGADO() {
        this.totalPaid = this.capitalPaid.add(this.interestPaid);
    }
}
