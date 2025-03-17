package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.InstallmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDTO {
    private Long id; // ID de la cuota
    private BigDecimal amount; // Monto total de la cuota
    private LocalDate dueDate; // Fecha de vencimiento
    private InstallmentStatus status; // Estado de la cuota
    private int installmentNumber; // Número de la cuota
    private BigDecimal capitalAmount; // Parte del capital a pagar en la cuota
    private BigDecimal interestAmount; // Parte del interés a pagar en la cuota
    private BigDecimal capitalPaid; // Capital pagado
    private BigDecimal interestPaid; // Interés pagado
    private BigDecimal capitalRemaining; // Monto restante de capital
    private BigDecimal interestRemaining; // Monto restante de interés
    private Long creditId; // ID del crédito al que pertenece la cuota
}