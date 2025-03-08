package com.prestamo.dalp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BigDecimal amount;  // Monto del préstamo

    @NotNull
    private BigDecimal interestRate;  // Tasa de interés

    @NotNull
    private LocalDate issueDate;  // Fecha de emisión del préstamo

    private LocalDate dueDate;  // Fecha de vencimiento

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Solo se usa para deserialización
    private Client client;  // Cliente asociado al préstamo

    @NotNull
    @Column(unique = true)  // Asegura que el código será único en la base de datos
    private String loanCode;  // Código único para cada préstamo

    @NotNull
    private BigDecimal interestAmount;  // Monto del interés calculado

    @NotNull
    private BigDecimal totalAmount;  // Monto total (monto del préstamo + monto del interés)

    @Enumerated(EnumType.STRING)
    @NotNull
    private LoanStatus status = LoanStatus.PENDING;  // Estado del préstamo (inicialmente PENDING)

    @Transient
    private Long clientId;  // Nuevo campo para incluir el id del cliente

    private Integer daysOverdue;  // Días de atraso en el pago

    @NotNull
    private BigDecimal interestPaid = BigDecimal.ZERO;  // Monto del interés pagado

    @NotNull
    private BigDecimal capitalPaid = BigDecimal.ZERO;  // Monto del capital pagado

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;  // Lista de pagos realizados sobre este préstamo

    @PrePersist
    @PreUpdate  // Asegura que el cálculo se haga también cuando se actualice el préstamo
    public void calculateAndGenerateCode() {
        // Genera un código único con el prefijo "LOAN-" seguido de una parte aleatoria (usando UUID)
        this.loanCode = "LOAN-" + generateUniqueCode();

        // Calcular monto de interés (Interés = monto * tasa de interés)
        this.interestAmount = this.amount.multiply(this.interestRate).divide(BigDecimal.valueOf(100));

        // Calcular monto total (Monto total = monto + monto del interés)
        this.totalAmount = this.amount.add(this.interestAmount);
    }

    private String generateUniqueCode() {
        // Generar un identificador único con una parte alfanumérica, por ejemplo con UUID
        return UUID.randomUUID().toString().substring(0, 8);  // Limitar a los primeros 8 caracteres
    }
}