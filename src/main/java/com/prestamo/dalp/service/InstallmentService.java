package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.InstallmentDTO;
import com.prestamo.dalp.DTO.PaymentDTO;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.InstallmentStatus;
import com.prestamo.dalp.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstallmentService {

    @Autowired
    private InstallmentRepository installmentRepository;

    public Installment processPayment(PaymentDTO paymentDTO) {
        Installment installment = installmentRepository.findById(paymentDTO.getInstallmentId())
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        // Verificar si el pago es completo o parcial
        BigDecimal totalDue = installment.getAmount();
        BigDecimal totalPaid = paymentDTO.getTotalPaid();

        // Priorizar el pago de intereses antes que el capital
        BigDecimal interestDue = installment.getInterestAmount();
        BigDecimal capitalDue = installment.getCapitalAmount();

        // Calcular el interés pagado
        BigDecimal interestPaid = paymentDTO.getInterestPaid().min(interestDue);
        BigDecimal capitalPaid = paymentDTO.getCapitalPaid().min(capitalDue);

        // Asegurar que el pago no exceda el monto total de la cuota
        if (totalPaid.compareTo(totalDue) > 0) {
            throw new RuntimeException("El monto pagado excede el monto total de la cuota.");
        }

        // Actualizar los montos pagados
        installment.setInterestPaid(installment.getInterestPaid().add(interestPaid));
        installment.setCapitalPaid(installment.getCapitalPaid().add(capitalPaid));

        // Verificar si la cuota está completamente pagada
        if (installment.getInterestPaid().compareTo(interestDue) >= 0 &&
                installment.getCapitalPaid().compareTo(capitalDue) >= 0) {
            installment.setStatus(InstallmentStatus.PAID);
        } else {
            installment.setStatus(InstallmentStatus.PARTIALLY_PAID);
        }

        // Registrar el medio de pago y la fecha de pago
        installment.setPaymentMethod(paymentDTO.getPaymentMethod());
        installment.setPaymentDate(paymentDTO.getPaymentDate());

        // Guardar la cuota actualizada
        return installmentRepository.save(installment);
    }

    public List<InstallmentDTO> getInstallmentsByCreditId(Long creditId) {
        List<Installment> installments = installmentRepository.findByCreditId(creditId);
        return installments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private InstallmentDTO convertToDTO(Installment installment) {
        InstallmentDTO dto = new InstallmentDTO();
        dto.setId(installment.getId());
        dto.setAmount(installment.getAmount());
        dto.setDueDate(installment.getDueDate());
        dto.setStatus(installment.getStatus());
        dto.setInstallmentNumber(installment.getInstallmentNumber());
        dto.setCapitalAmount(installment.getCapitalAmount());
        dto.setInterestAmount(installment.getInterestAmount());
        dto.setCapitalPaid(installment.getCapitalPaid());
        dto.setInterestPaid(installment.getInterestPaid());

        return dto;
    }
}