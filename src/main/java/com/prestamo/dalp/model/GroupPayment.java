package com.prestamo.dalp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "group_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de servicio (ej. "AGUA", "LUZ", "NETFLIX", etc.)
    @NotNull
    private String serviceType;

    // Descripción opcional (ej. "Pago de agua de febrero")
    private String description;

    // Monto total de la factura del servicio
    @NotNull
    private BigDecimal totalAmount;

    // Fecha y hora en que se realizó el pago
    @NotNull
    private LocalDateTime paymentDate;

    // Método de pago utilizado
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Estado del reembolso: por ejemplo, "PENDIENTE" (aún no se ha reembolsado todo) o "COMPLETADO"
    @NotNull
    private String status;

    // Monto total reembolsado hasta el momento
    @NotNull
    private BigDecimal reimbursedAmount = BigDecimal.ZERO;

    // Saldo pendiente de reembolso (totalAmount - reimbursedAmount)
    @NotNull
    private BigDecimal pendingReimbursement;

    // Cliente que realizó el pago completo (pagador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private Client payer;

    // Lista de contribuciones de cada amigo
    @OneToMany(mappedBy = "groupPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupPaymentContribution> contributions;

    // Identificador único para el pago grupal
    @Column(unique = true, updatable = false)
    private String paymentIdentifier;

    @PrePersist
    public void prePersist() {
        if (this.paymentIdentifier == null) {
            this.paymentIdentifier = "GP-" + UUID.randomUUID().toString().substring(0, 8);
        }
        if (this.pendingReimbursement == null) {
            this.pendingReimbursement = this.totalAmount;
        }
        if (this.reimbursedAmount == null) {
            this.reimbursedAmount = BigDecimal.ZERO;
        }
    }

}
