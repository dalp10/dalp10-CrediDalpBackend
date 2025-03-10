package com.prestamo.dalp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPaymentContributionDTO {
    private Long id;
    private Long clientId;
    private BigDecimal amountPaid;
    private LocalDateTime contributionDate;
    // Puedes dejarlo como String o como enum; en este ejemplo lo usamos como String
    private String paymentMethod;
}
