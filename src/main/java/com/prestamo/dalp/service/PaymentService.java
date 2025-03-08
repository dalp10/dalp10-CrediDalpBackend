package com.prestamo.dalp.service;

import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.model.LoanStatus;
import com.prestamo.dalp.model.Payment;
import com.prestamo.dalp.repository.LoanRepository;
import com.prestamo.dalp.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private LoanRepository loanRepository;

    // Realizar un pago sobre un préstamo
    public String makePayment(Long loanId, Payment payment) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();

            // Calcular días de atraso
            LocalDate today = LocalDate.now();
            if (loan.getDueDate() != null && today.isAfter(loan.getDueDate())) {
                long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                loan.setDaysOverdue((int) daysOverdue);
            }

            // Aplicar el pago primero al interés y luego al capital
            BigDecimal remainingInterest = loan.getInterestAmount().subtract(loan.getInterestPaid());
            BigDecimal remainingCapital = loan.getAmount().subtract(loan.getCapitalPaid());

            if (payment.getInterestPaid().compareTo(remainingInterest) > 0) {
                payment.setInterestPaid(remainingInterest);
            }
            if (payment.getCapitalPaid().compareTo(remainingCapital) > 0) {
                payment.setCapitalPaid(remainingCapital);
            }

            // Guardar el pago
            payment.setLoan(loan);
            paymentRepository.save(payment);

            // Actualizar el préstamo con los montos pagados
            loan.setInterestPaid(loan.getInterestPaid().add(payment.getInterestPaid()));
            loan.setCapitalPaid(loan.getCapitalPaid().add(payment.getCapitalPaid()));

            // Si el préstamo está completamente pagado, cambiar su estado
            if (loan.getCapitalPaid().compareTo(loan.getAmount()) >= 0 &&
                    loan.getInterestPaid().compareTo(loan.getInterestAmount()) >= 0) {
                loan.setStatus(LoanStatus.PAID);
            }

            loanRepository.save(loan);
            return "Pago realizado con éxito";
        } else {
            return "Error: Préstamo no encontrado";
        }
    }

    // Obtener los pagos de un préstamo por su ID
    public List<Payment> getPaymentsByLoanId(Long loanId) {
        return paymentRepository.findByLoanId(loanId);
    }
}