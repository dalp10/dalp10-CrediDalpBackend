package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.InstallmentDTO;
import com.prestamo.dalp.DTO.PaymentCreditDTO;
import com.prestamo.dalp.mapper.InstallmentMapper;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.InstallmentStatus;
import com.prestamo.dalp.model.PaymentCredit;
import com.prestamo.dalp.repository.InstallmentRepository;
import com.prestamo.dalp.repository.PaymentCreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstallmentService {

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private PaymentCreditRepository paymentCreditRepository;

    /**
     * Procesa el pago de una cuota.
     *
     * @param paymentCreditDTO Datos del pago.
     * @return La cuota actualizada.
     * @throws IllegalArgumentException Si los datos del pago son inválidos.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public Installment processPayment(PaymentCreditDTO paymentCreditDTO) {
        // Validar que el ID de la cuota y el monto a pagar no sean nulos
        if (paymentCreditDTO.getInstallmentId() == null || paymentCreditDTO.getTotalPaid() == null) {
            throw new IllegalArgumentException("El ID de la cuota y el monto a pagar no pueden ser nulos");
        }

        // Buscar la cuota por su ID
        Installment installment = installmentRepository.findById(paymentCreditDTO.getInstallmentId())
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        // Validar que la cuota no esté completamente pagada
        if (installment.getStatus() == InstallmentStatus.PAGADA) {
            throw new IllegalArgumentException("La cuota ya está completamente pagada");
        }

        // Calcular los montos adeudados de capital e interés
        BigDecimal interestDue = installment.getInterestAmount().subtract(installment.getInterestPaid());
        BigDecimal capitalDue = installment.getCapitalAmount().subtract(installment.getCapitalPaid());
        BigDecimal totalDue = interestDue.add(capitalDue);

        // Validar que el monto pagado no exceda el total adeudado
        if (paymentCreditDTO.getTotalPaid().compareTo(totalDue) > 0) {
            throw new IllegalArgumentException("El monto pagado excede el saldo total pendiente");
        }

        // Calcular cuánto se destina a interés y cuánto a capital
        BigDecimal interestToPay = paymentCreditDTO.getTotalPaid().compareTo(interestDue) >= 0 ? interestDue : paymentCreditDTO.getTotalPaid();
        BigDecimal capitalToPay = paymentCreditDTO.getTotalPaid().subtract(interestToPay);

        // Actualizar los montos pagados en la cuota
        installment.setInterestPaid(installment.getInterestPaid().add(interestToPay));
        installment.setCapitalPaid(installment.getCapitalPaid().add(capitalToPay));
        installment.setPaymentDate(paymentCreditDTO.getPaymentDate());

        // Actualizar los montos restantes y el estado de la cuota
        installment.updateRemainingAmounts();
        if (installment.getCapitalRemaining().compareTo(BigDecimal.ZERO) == 0
                && installment.getInterestRemaining().compareTo(BigDecimal.ZERO) == 0) {
            installment.setStatus(InstallmentStatus.PAGADA);
        } else {
            installment.setStatus(InstallmentStatus.PARCIALMENTE_PAGADA);
        }

        // Guardar la cuota actualizada
        Installment updatedInstallment = installmentRepository.save(installment);

        // Registrar el pago en PaymentCredit
        PaymentCredit paymentCredit = new PaymentCredit();
        paymentCredit.setCredit(installment.getCredit()); // Asignar el crédito
        paymentCredit.setInstallment(installment); // Asignar la cuota
        paymentCredit.setCapitalPaid(capitalToPay); // Monto pagado de capital
        paymentCredit.setInterestPaid(interestToPay); // Monto pagado de interés
        paymentCredit.setTotalPaid(paymentCreditDTO.getTotalPaid()); // Monto total pagado
        paymentCredit.setPaymentDate(paymentCreditDTO.getPaymentDate()); // Fecha de pago
        paymentCredit.setPaymentMethod(paymentCreditDTO.getPaymentMethod()); // Método de pago
        paymentCredit.setInstallmentNumber(installment.getInstallmentNumber()); // Número de cuota

        // Guardar el registro de pago
        paymentCreditRepository.save(paymentCredit);

        // Retornar la cuota actualizada
        return updatedInstallment;
    }

    /**
     * Obtiene una cuota por su ID.
     *
     * @param installmentId ID de la cuota.
     * @return La cuota en formato DTO.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public InstallmentDTO getInstallmentById(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
        return InstallmentMapper.toDTO(installment);
    }

    /**
     * Actualiza una cuota existente.
     *
     * @param installmentId ID de la cuota a actualizar.
     * @param installmentDTO Datos actualizados de la cuota.
     * @return La cuota actualizada en formato DTO.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public InstallmentDTO updateInstallment(Long installmentId, InstallmentDTO installmentDTO) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        // Actualizar los campos de la cuota
        installment.setAmount(installmentDTO.getAmount());
        installment.setDueDate(installmentDTO.getDueDate());
        installment.setStatus(installmentDTO.getStatus());
        installment.setCapitalAmount(installmentDTO.getCapitalAmount());
        installment.setInterestAmount(installmentDTO.getInterestAmount());
        installment.setCapitalPaid(installmentDTO.getCapitalPaid());
        installment.setInterestPaid(installmentDTO.getInterestPaid());

        // Guardar y retornar la cuota actualizada
        Installment updatedInstallment = installmentRepository.save(installment);
        return InstallmentMapper.toDTO(updatedInstallment);
    }

    /**
     * Elimina una cuota por su ID.
     *
     * @param installmentId ID de la cuota a eliminar.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public void deleteInstallment(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
        installmentRepository.delete(installment);
    }

    /**
     * Calcula el saldo pendiente de una cuota.
     *
     * @param installmentId ID de la cuota.
     * @return El saldo pendiente (capital + interés).
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public BigDecimal calculateRemainingBalance(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        BigDecimal capitalRemaining = installment.getCapitalAmount().subtract(installment.getCapitalPaid());
        BigDecimal interestRemaining = installment.getInterestAmount().subtract(installment.getInterestPaid());

        return capitalRemaining.add(interestRemaining);
    }

    /**
     * Obtiene todas las cuotas de un crédito, opcionalmente filtradas por estado.
     *
     * @param creditId ID del crédito.
     * @param status Estado de las cuotas (opcional).
     * @return Lista de cuotas en formato DTO.
     */
    public List<InstallmentDTO> getInstallmentsByCreditIdAndStatus(Long creditId, InstallmentStatus status) {
        List<Installment> installments;
        if (status != null) {
            installments = installmentRepository.findByCreditIdAndStatus(creditId, status);
        } else {
            installments = installmentRepository.findByCreditId(creditId);
        }
        return installments.stream()
                .map(InstallmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recalcula el estado de una cuota basado en los montos pagados.
     *
     * @param installmentId ID de la cuota.
     * @return La cuota actualizada en formato DTO.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public InstallmentDTO recalculateInstallmentStatus(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        // Recalcular montos restantes y estado
        installment.updateRemainingAmounts();
        if (installment.getCapitalRemaining().compareTo(BigDecimal.ZERO) == 0
                && installment.getInterestRemaining().compareTo(BigDecimal.ZERO) == 0) {
            installment.setStatus(InstallmentStatus.PAGADA);
        } else {
            installment.setStatus(InstallmentStatus.PARCIALMENTE_PAGADA);
        }

        // Guardar y retornar la cuota actualizada
        Installment updatedInstallment = installmentRepository.save(installment);
        return InstallmentMapper.toDTO(updatedInstallment);
    }

    /**
     * Verifica si una cuota está atrasada.
     *
     * @param installmentId ID de la cuota.
     * @return true si la cuota está atrasada, false en caso contrario.
     * @throws RuntimeException Si la cuota no se encuentra.
     */
    public boolean isInstallmentVENCIDO(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        LocalDate today = LocalDate.now();
        return today.isAfter(installment.getDueDate()) && installment.getStatus() != InstallmentStatus.PAGADA;
    }


    /**
     * Obtiene todas las cuotas de un crédito por su ID.
     *
     * @param creditId ID del crédito.
     * @return Lista de cuotas en formato DTO.
     */
    public List<InstallmentDTO> getInstallmentsByCreditId(Long creditId) {
        // Buscar las cuotas por el ID del crédito
        List<Installment> installments = installmentRepository.findByCreditId(creditId);

        // Convertir las cuotas a DTO
        return installments.stream()
                .map(InstallmentMapper::toDTO)
                .collect(Collectors.toList());
    }
}