package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.PaymentMethod;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentCreditDTO {
    private Long id;
    private Long creditId;           // ID del crédito asociado
    private Long installmentId;      // ID de la cuota
    private Integer installmentNumber; // Número de cuota que se está pagando
    private BigDecimal capitalPaid;  // Monto pagado del capital
    private BigDecimal interestPaid; // Monto pagado del interés
    private BigDecimal totalPaid;    // Monto total pagado (capital + interés)
    private LocalDate paymentDate;   // Fecha del pago
    private PaymentMethod paymentMethod; // Medio de pago (EFECTIVO, TRANSFERENCIA, etc.)
}
