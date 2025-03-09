package com.prestamo.dalp.service;

import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.model.LoanStatus;
import com.prestamo.dalp.model.Payment;
import com.prestamo.dalp.repository.LoanHistoryRepository;
import com.prestamo.dalp.repository.LoanRepository;
import com.prestamo.dalp.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.prestamo.dalp.model.LoanHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanHistoryRepository loanHistoryRepository;

    /**
     * Procesa un pago sobre el préstamo identificado por loanId.
     * <p>
     * Lógica principal:
     * <ul>
     *   <li>Calcula días de atraso si la fecha actual supera la fecha de vencimiento.</li>
     *   <li>Verifica que el pago no exceda el saldo pendiente de capital e interés.</li>
     *   <li>Registra el pago en la base de datos.</li>
     *   <li>Actualiza los montos pagados acumulados (capitalPaid, interestPaid).</li>
     *   <li>Recalcula los campos remainingCapital, remainingInterest y totalAmount (saldo pendiente).</li>
     *   <li>Si la deuda se salda, marca el préstamo como PAID.</li>
     *   <li>Guarda un registro de historial (LoanHistory) para conservar la trazabilidad.</li>
     * </ul>
     *
     * @param loanId   ID del préstamo a pagar.
     * @param payment  Objeto Payment que contiene los montos pagados de interés y capital.
     * @return Un mensaje indicando si el pago se realizó con éxito o si ocurrió algún error.
     */
    public String makePayment(Long loanId, Payment payment) {
        // 1. Verificar que el préstamo exista
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (!optionalLoan.isPresent()) {
            return "Error: Préstamo no encontrado";
        }
        Loan loan = optionalLoan.get();

        // 2. Calcular días de atraso, si corresponde
        LocalDate today = LocalDate.now();
        if (loan.getDueDate() != null && today.isAfter(loan.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), today);
            loan.setDaysOverdue((int) daysOverdue);
        }

        // 3. Obtener lo pendiente antes de este pago
        //    Se usan remainingInterest y remainingCapital, inicializados al crear el Loan
        BigDecimal remainingInterest = loan.getRemainingInterest();
        BigDecimal remainingCapital = loan.getRemainingCapital();

        // 4. Ajustar el pago recibido para que no exceda lo pendiente de interés y capital
        if (payment.getInterestPaid().compareTo(remainingInterest) > 0) {
            payment.setInterestPaid(remainingInterest);
        }
        if (payment.getCapitalPaid().compareTo(remainingCapital) > 0) {
            payment.setCapitalPaid(remainingCapital);
        }

        // 5. Guardar el pago asociado al préstamo
        payment.setLoan(loan);
        paymentRepository.save(payment);

        // 6. Actualizar los montos pagados acumulados
        loan.setInterestPaid(loan.getInterestPaid().add(payment.getInterestPaid()));
        loan.setCapitalPaid(loan.getCapitalPaid().add(payment.getCapitalPaid()));

        // 7. Recalcular los saldos pendientes
        BigDecimal newRemainingInterest = remainingInterest.subtract(payment.getInterestPaid());
        BigDecimal newRemainingCapital = remainingCapital.subtract(payment.getCapitalPaid());
        loan.setRemainingInterest(newRemainingInterest);
        loan.setRemainingCapital(newRemainingCapital);

        // 8. Calcular la nueva deuda total (capital pendiente + interés pendiente)
        BigDecimal newTotalDebt = newRemainingCapital.add(newRemainingInterest);
        loan.setTotalAmount(newTotalDebt);

        // 9. Si la deuda se ha saldado por completo, actualizar el estado a PAID
        if (newTotalDebt.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.PAID);
        }

        // 10. Guardar el préstamo con los valores actualizados
        loanRepository.save(loan);

        // 11. Registrar el historial del pago
        LoanHistory history = new LoanHistory();
        history.setLoan(loan);  // La instancia ya está en persistencia
        history.setTotalAmount(loan.getTotalAmount());  // Saldo pendiente actual
        history.setAmount(loan.getAmount());            // Capital original
        history.setInterestAmount(loan.getInterestAmount()); // Interés original
        history.setCapitalPaid(loan.getCapitalPaid());  // Capital pagado acumulado
        history.setInterestPaid(loan.getInterestPaid()); // Interés pagado acumulado
        history.setTimestamp(LocalDateTime.now());
        loanHistoryRepository.save(history);

        // 12. Retornar mensaje de éxito
        return "Pago realizado con éxito";
    }



    // Obtener los pagos de un préstamo por su ID
    public List<Payment> getPaymentsByLoanId(Long loanId) {
        return paymentRepository.findByLoanId(loanId);
    }
}