package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.CreditStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

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

}
