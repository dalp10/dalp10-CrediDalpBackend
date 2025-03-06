package com.prestamo.dalp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "installments")
@Data
@NoArgsConstructor
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dueDate; // Fecha de vencimiento de la cuota

    @Column(nullable = false)
    private BigDecimal amount; // Monto a pagar en la cuota

    @Column(nullable = false)
    private BigDecimal capitalAmount; // Parte del capital a pagar en la cuota

    @Column(nullable = false)
    private BigDecimal interestAmount; // Parte del interés a pagar en la cuota

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status = InstallmentStatus.PENDING; // Estado de la cuota (PENDING, PAID, OVERDUE)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", nullable = false)
    private Credit credit; // Crédito asociado a la cuota
}