package com.prestamo.dalp.service;

import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.PaymentCredit;
import com.prestamo.dalp.model.PaymentMethod;
import com.prestamo.dalp.repository.CreditRepository;
import com.prestamo.dalp.repository.PaymentCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentCreditService {

    @Autowired
    private PaymentCreditRepository paymentCreditRepository;

    @Autowired
    private CreditRepository creditRepository;

    /**
     * Procesa un pago para un crédito.
     *
     * @param creditId          ID del crédito
     * @param installmentNumber Número de cuota que se está pagando
     * @param interestToPay     Monto destinado a interés
     * @param capitalToPay      Monto destinado a capital
     * @param totalPAGADO         Monto total pagado
     * @param paymentDate       Fecha del pago
     * @param paymentMethodStr  Método de pago (como String)
     * @return PaymentCredit guardado
     */
    public PaymentCredit makePayment(Long creditId,
                                     Integer installmentNumber,
                                     BigDecimal interestToPay,
                                     BigDecimal capitalToPay,
                                     BigDecimal totalPAGADO,
                                     LocalDate paymentDate,
                                     String paymentMethodStr) {
        // Buscar la entidad Credit
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));

        PaymentCredit paymentCredit = new PaymentCredit();
        paymentCredit.setCredit(credit);
        paymentCredit.setInstallmentNumber(installmentNumber);
        paymentCredit.setInterestPaid(interestToPay);
        paymentCredit.setCapitalPaid(capitalToPay);
        paymentCredit.setTotalPaid(totalPAGADO);
        paymentCredit.setPaymentDate(paymentDate);
        paymentCredit.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr.toUpperCase()));

        return paymentCreditRepository.save(paymentCredit);
    }

    // Método para obtener los pagos de un crédito por su ID
    public List<PaymentCredit> getPaymentsByCreditId(Long creditId) {
        return paymentCreditRepository.findByCreditId(creditId);
    }
}
