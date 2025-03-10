package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPaymentDTO {
    private Long id;
    private String serviceType;
    private String description;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String status;
    private BigDecimal reimbursedAmount;
    private BigDecimal pendingReimbursement;
    // Id del pagador
    private Long payerId;

    // Lista de contribuciones
    private List<GroupPaymentContributionDTO> contributions;
}
