package com.prestamo.dalp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "installments")
@Data
@NoArgsConstructor
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate; // Fecha de vencimiento de la cuota

    @Column(nullable = false)
    private BigDecimal amount; // Monto a pagar en la cuota

    @Column(nullable = false)
    private BigDecimal capitalAmount; // Parte del capital a pagar en la cuota

    @Column(nullable = false)
    private BigDecimal interestAmount; // Parte del interés a pagar en la cuota

    @Column(nullable = false)
    private BigDecimal capitalPaid = BigDecimal.ZERO; // Capital pagado

    @Column(nullable = false)
    private BigDecimal interestPaid = BigDecimal.ZERO; // Interés pagado

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status = InstallmentStatus.PENDING; // Estado de la cuota (PENDING, PAID, PARTIALLY_PAID, OVERDUE)

    @Column
    private String paymentMethod; // Medio de pago (efectivo, transferencia, Yape, Plin)

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate; // Fecha de pago

    @Column(nullable = false)
    private int installmentNumber; // Número de cuota (1, 2, 3, ...)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", nullable = false)
    @JsonBackReference // Ignora la serialización de la relación en este lado
    private Credit credit; // Crédito asociado a la cuota

}