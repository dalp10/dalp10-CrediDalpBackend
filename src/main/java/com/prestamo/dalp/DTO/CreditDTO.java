package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.CreditStatus;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.Client;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data   // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los parámetros

public class CreditDTO {

    private Long id;
    private BigDecimal capitalAmount;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int gracePeriodDays;
    private CreditStatus status;

    // Campos faltantes
    private BigDecimal tea; // Tasa Efectiva Anual
    private String code; // Código del crédito
    private int installmentNumber; // Número de cuota
    private List<Installment> installments; // Lista de cuotas
    private Client client; // Cliente asociado al crédito
}
