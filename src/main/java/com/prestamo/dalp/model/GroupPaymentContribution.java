package com.prestamo.dalp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_payment_contributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPaymentContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente que contribuye
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Monto que el cliente aporta
    @NotNull
    private BigDecimal amountPaid;

    // Fecha en que se realizó la contribución
    @NotNull
    private LocalDateTime contributionDate;

    // Medio de pago usado para esta contribución (nuevo campo)
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Relación con el pago grupal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_payment_id", nullable = false)
    private GroupPayment groupPayment;
}
