package com.prestamo.dalp.model;

public enum LoanStatus {
    PENDING,    // Préstamo pendiente de aprobación o procesamiento
    APPROVED,   // Préstamo aprobado
    REJECTED,   // Préstamo rechazado
    PAID,       // Préstamo pagado
    OVERDUE     // Préstamo vencido
}
