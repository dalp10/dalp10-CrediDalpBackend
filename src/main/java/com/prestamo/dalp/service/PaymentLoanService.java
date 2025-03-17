package com.prestamo.dalp.service;

import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.model.LoanStatus;
import com.prestamo.dalp.model.PaymentLoan;
import com.prestamo.dalp.repository.LoanHistoryRepository;
import com.prestamo.dalp.repository.LoanRepository;
import com.prestamo.dalp.repository.PaymentLoanRepository;
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
public class PaymentLoanService {

    @Autowired
    private PaymentLoanRepository paymentLoanRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanHistoryRepository loanHistoryRepository;

    /**
     * Procesa un pago sobre el préstamo identificado por loanId.
     * Lógica principal:
     *   - Calcula días de atraso.
     *   - Ajusta los montos pagados.
     *   - Registra el pago en la base de datos usando PaymentLoan.
     *   - Actualiza el préstamo y registra el historial.
     *
     * @param loanId      ID del préstamo a pagar.
     * @param paymentLoan Objeto PaymentLoan con los montos y datos del pago.
     * @return Mensaje indicando el resultado.
     */
    public String makePayment(Long loanId, PaymentLoan paymentLoan) {
        // 1. Verificar que el préstamo exista
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (!optionalLoan.isPresent()) {
            return "Error: Préstamo no encontrado";
        }
        Loan loan = optionalLoan.get();

        // 2. Calcular días de atraso si corresponde
        LocalDate today = LocalDate.now();
        if (loan.getDueDate() != null && today.isAfter(loan.getDueDate())) {
            long daysVENCIDO = ChronoUnit.DAYS.between(loan.getDueDate(), today);
            loan.setDaysOverdue((int) daysVENCIDO);
        }

        // 3. Obtener lo pendiente antes del pago
        BigDecimal remainingInterest = loan.getRemainingInterest();
        BigDecimal remainingCapital = loan.getRemainingCapital();

        // 4. Ajustar el pago para no exceder lo pendiente
        if (paymentLoan.getInterestPaid().compareTo(remainingInterest) > 0) {
            paymentLoan.setInterestPaid(remainingInterest);
        }
        if (paymentLoan.getCapitalPaid().compareTo(remainingCapital) > 0) {
            paymentLoan.setCapitalPaid(remainingCapital);
        }

        // 5. Asociar el préstamo al pago y guardar
        paymentLoan.setLoan(loan);
        paymentLoanRepository.save(paymentLoan);

        // 6. Actualizar montos pagados acumulados en el préstamo
        loan.setInterestPaid(loan.getInterestPaid().add(paymentLoan.getInterestPaid()));
        loan.setCapitalPaid(loan.getCapitalPaid().add(paymentLoan.getCapitalPaid()));

        // 7. Recalcular saldos pendientes
        BigDecimal newRemainingInterest = remainingInterest.subtract(paymentLoan.getInterestPaid());
        BigDecimal newRemainingCapital = remainingCapital.subtract(paymentLoan.getCapitalPaid());
        loan.setRemainingInterest(newRemainingInterest);
        loan.setRemainingCapital(newRemainingCapital);

        // 8. Calcular nueva deuda total
        BigDecimal newTotalDebt = newRemainingCapital.add(newRemainingInterest);
        loan.setTotalAmount(newTotalDebt);

        // 9. Si se saldó la deuda, actualizar estado a PAGADO
        if (newTotalDebt.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.PAGADO);
        }

        // 10. Guardar el préstamo actualizado
        loanRepository.save(loan);

        // 11. Registrar historial del pago
        LoanHistory history = new LoanHistory();
        history.setLoan(loan);
        history.setTotalAmount(loan.getTotalAmount());
        history.setAmount(loan.getAmount());
        history.setInterestAmount(loan.getInterestAmount());
        history.setCapitalPaid(loan.getCapitalPaid());
        history.setInterestPaid(loan.getInterestPaid());
        history.setTimestamp(LocalDateTime.now());
        loanHistoryRepository.save(history);

        return "Pago realizado con éxito";
    }

    // Método para obtener pagos de un préstamo
    public List<PaymentLoan> getPaymentsByLoanId(Long loanId) {
        return paymentLoanRepository.findByLoanId(loanId);
    }
}
