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
    private BigDecimal amount;         // Monto original del préstamo (capital)

    @NotNull
    private BigDecimal interestRate;   // Tasa de interés original

    @NotNull
    private LocalDate issueDate;       // Fecha de emisión del préstamo

    private LocalDate dueDate;         // Fecha de vencimiento (opcional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client client;

    @NotNull
    @Column(unique = true)
    private String loanCode;           // Código único del préstamo

    @NotNull
    private BigDecimal interestAmount; // Interés original calculado (amount * interestRate / 100)

    @NotNull
    private BigDecimal totalAmount;    // Saldo pendiente (se irá recalculando)

    @NotNull
    private BigDecimal interestPaid = BigDecimal.ZERO;  // Interés pagado acumulado

    @NotNull
    private BigDecimal capitalPaid = BigDecimal.ZERO;   // Capital pagado acumulado

    // NUEVOS CAMPOS: capital e interés pendiente
    @NotNull
    private BigDecimal remainingCapital = BigDecimal.ZERO;

    @NotNull
    private BigDecimal remainingInterest = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LoanStatus status = LoanStatus.PENDING;

    private Integer daysOverdue;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @PrePersist
    @PreUpdate
    public void calculateAndGenerateCode() {
        // Solo generar el código si es nuevo (ID nulo) y aún no se ha asignado.
        if (this.id == null && this.loanCode == null) {
            this.loanCode = "LOAN-" + generateUniqueCode();
        }

        // Si es creación (ID nulo), inicializar los valores originales y pendientes.
        if (this.id == null) {
            // Inicializar interestAmount y totalAmount solo si aún no se han establecido.
            if (this.interestAmount == null || this.interestAmount.compareTo(BigDecimal.ZERO) == 0) {
                this.interestAmount = this.amount.multiply(this.interestRate).divide(BigDecimal.valueOf(100));
            }
            if (this.totalAmount == null || this.totalAmount.compareTo(BigDecimal.ZERO) == 0) {
                this.totalAmount = this.amount.add(this.interestAmount);
            }

            // Inicializar remainingCapital y remainingInterest con los valores originales.
            if (this.remainingCapital == null || this.remainingCapital.compareTo(BigDecimal.ZERO) == 0) {
                this.remainingCapital = this.amount;
            }
            if (this.remainingInterest == null || this.remainingInterest.compareTo(BigDecimal.ZERO) == 0) {
                this.remainingInterest = this.interestAmount;
            }
        }
        // Si no es creación (ID ya asignado), no se reinicializan estos campos.
    }


    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
