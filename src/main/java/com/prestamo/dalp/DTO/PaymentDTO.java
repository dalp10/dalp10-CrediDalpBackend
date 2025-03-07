package com.prestamo.dalp.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentDTO {
    private Long installmentId; // ID de la cuota
    private BigDecimal capitalPaid; // Saldo capital pagado
    private BigDecimal interestPaid; // Interés pagado
    private int installmentNumber; // Número de cuota pagada
    private String paymentMethod; // Medio de pago (efectivo, transferencia, Yape, Plin)
    private LocalDate paymentDate; // Fecha de pago
    private BigDecimal totalPaid; // Monto total pagado
}