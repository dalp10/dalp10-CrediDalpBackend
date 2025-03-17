package com.prestamo.dalp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_credits")
@Data
@NoArgsConstructor
public class PaymentCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", nullable = false)
    private Credit credit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installment_id", nullable = false)
    private Installment installment;

    @Column(nullable = false)
    private BigDecimal capitalPaid;

    @Column(nullable = false)
    private BigDecimal interestPaid;

    @Column(nullable = false)
    private BigDecimal totalPaid;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING) // Usar el enum PaymentMethod
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private int installmentNumber;
}
