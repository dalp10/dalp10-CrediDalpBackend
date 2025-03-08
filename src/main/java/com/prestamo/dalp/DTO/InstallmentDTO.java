package com.prestamo.dalp.DTO;
import com.prestamo.dalp.model.InstallmentStatus;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data   // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los parámetros

public class InstallmentDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate dueDate;
    private InstallmentStatus status;
    private int installmentNumber ;
    private BigDecimal capitalAmount; // Parte del capital a pagar en la cuota
    private BigDecimal interestAmount; // Parte del interés a pagar en la cuota
    private BigDecimal capitalPaid = BigDecimal.ZERO; // Capital pagado
    private BigDecimal interestPaid = BigDecimal.ZERO; // Interés pagado

    // Getters y setters
}
