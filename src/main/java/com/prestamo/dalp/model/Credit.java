package com.prestamo.dalp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "credits")
@Data
@NoArgsConstructor
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal capitalAmount; // Saldo capital

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate; // Fecha de inicio del crédito

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate; // Fecha de finalización del crédito

    @Column(nullable = false)
    private int gracePeriodDays; // Período de gracia en días

    @Column(nullable = false)
    private BigDecimal tea; // Tasa Efectiva Anual

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference // Ignora la serialización de esta relación
    private Client client; // Cliente asociado al crédito

    @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments; // Lista de cuotas

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditStatus status = CreditStatus.ACTIVO; // Estado del crédito (ACTIVO, PAGADO, VENCIDO, etc.)

    @Column(nullable = false, unique = true)
    private String code; // Código autogenerado (CRE-XXX)

    @Column(nullable = false)
    private int installmentNumber; // Número de cuota

    // Método para calcular la fecha fin automáticamente
    public LocalDate calculateEndDate(LocalDate firstPaymentDate, int numberOfInstallments) {
        return firstPaymentDate.plusMonths(numberOfInstallments - 1);
    }

    public BigDecimal calculateTotalInterest() {
        return capitalAmount.multiply(tea).divide(BigDecimal.valueOf(100));
    }
}