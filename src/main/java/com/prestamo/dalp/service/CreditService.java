package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.CreditDTO;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.InstallmentStatus;
import com.prestamo.dalp.repository.CreditRepository;
import com.prestamo.dalp.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    // Método para crear un nuevo crédito con sus cuotas
    public Credit createCredit(Credit credit, int numberOfInstallments, int gracePeriodDays, BigDecimal tea, LocalDate firstPaymentDate) {
        // Calcular la fecha fin automáticamente
        LocalDate endDate = credit.calculateEndDate(firstPaymentDate, numberOfInstallments);
        credit.setEndDate(endDate); // Asignar la fecha fin calculada

        // Guardar el crédito con la TEA
        credit.setTea(tea);
        Credit savedCredit = creditRepository.save(credit);

        // Calcular la tasa mensual a partir de la TEA
        BigDecimal monthlyRate = calculateMonthlyRate(tea);

        // Calcular el interés de gracia
        BigDecimal graceInterest = calculateGraceInterest(savedCredit.getCapitalAmount(), monthlyRate, gracePeriodDays);

        // Añadir el interés de gracia al costo total del préstamo
        BigDecimal totalCost = savedCredit.getCapitalAmount().add(graceInterest);

        // Calcular el monto de la cuota constante (amortización francesa) sobre el costo total
        BigDecimal installmentAmount = calculateConstantInstallment(totalCost, monthlyRate, numberOfInstallments);

        // Establecer la fecha de la primera cuota
        LocalDate dueDate = firstPaymentDate;

        BigDecimal remainingCapital = totalCost;

        for (int i = 0; i < numberOfInstallments; i++) {
            Installment installment = new Installment();

            // Calcular el interés de la cuota
            BigDecimal interestAmount = remainingCapital.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            // Calcular el capital amortizado
            BigDecimal capitalPayment = installmentAmount.subtract(interestAmount);

            // Ajustar la última cuota para asegurar que el capital restante se amortice completamente
            if (i == numberOfInstallments - 1) {
                capitalPayment = remainingCapital; // Asegurar que el capital restante se amortice completamente
            }

            // Verificar que el interés no sea negativo
            if (interestAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("El interés no puede ser negativo. Revise los cálculos.");
            }

            // Actualizar el capital restante
            remainingCapital = remainingCapital.subtract(capitalPayment);

            // Configurar la cuota con la fecha fija de vencimiento
            installment.setDueDate(calculateDueDate(dueDate, i)); // Fecha fija de pago
            installment.setAmount(installmentAmount); // Monto de la cuota (constante)
            installment.setCapitalAmount(capitalPayment); // Monto del capital
            installment.setInterestAmount(interestAmount); // Monto del interés
            installment.setCredit(savedCredit); // Asignar el crédito a la cuota

            // Guardar la cuota en la base de datos
            installmentRepository.save(installment);

            // Ajustar la fecha para la siguiente cuota
            dueDate = dueDate.plusMonths(1); // Avanzamos a la siguiente fecha del mes para la próxima cuota
        }

        return savedCredit;
    }

    // Método para calcular la cuota constante (amortización francesa)
    private BigDecimal calculateConstantInstallment(BigDecimal capitalAmount, BigDecimal monthlyRate, int numberOfInstallments) {
        BigDecimal numerator = capitalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(
                        BigDecimal.ONE.add(monthlyRate).pow(numberOfInstallments), 10, RoundingMode.HALF_UP
                )
        );
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    // Método para calcular la tasa mensual a partir de la TEA
    private BigDecimal calculateMonthlyRate(BigDecimal tea) {
        BigDecimal one = BigDecimal.ONE;
        BigDecimal teaDividedBy100 = tea.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal power = one.add(teaDividedBy100);
        BigDecimal exponent = BigDecimal.ONE.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP); // 12 meses en un año
        return BigDecimal.valueOf(Math.pow(power.doubleValue(), exponent.doubleValue())).subtract(one);
    }

    // Método para calcular el interés de gracia
    private BigDecimal calculateGraceInterest(BigDecimal capitalAmount, BigDecimal monthlyRate, int gracePeriodDays) {
        BigDecimal gracePeriodMonths = BigDecimal.valueOf(gracePeriodDays).divide(BigDecimal.valueOf(360), 10, RoundingMode.HALF_UP);
        return capitalAmount.multiply(monthlyRate).multiply(gracePeriodMonths).setScale(2, RoundingMode.HALF_UP);
    }

    // Método para calcular la fecha de vencimiento
    private LocalDate calculateDueDate(LocalDate firstPaymentDate, int installmentIndex) {
        LocalDate dueDate = firstPaymentDate.plusMonths(installmentIndex);

        // Ajustar la fecha si el día no existe en el mes
        int dayOfMonth = dueDate.getDayOfMonth();
        int maxDaysInMonth = dueDate.lengthOfMonth();

        // Si el día del mes no existe en este mes, ajustamos al último día del mes
        if (dayOfMonth > maxDaysInMonth) {
            dueDate = dueDate.withDayOfMonth(maxDaysInMonth); // Ajustamos al último día del mes
        }

        return dueDate;
    }

    // Método para obtener todos los créditos de un cliente
    public List<CreditDTO> getCreditsByClient(Long clientId) {
        List<Credit> credits = creditRepository.findByClientId(clientId);
        return credits.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para pagar una cuota
    public Installment payInstallment(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
        installment.setStatus(InstallmentStatus.PAID);
        return installmentRepository.save(installment);
    }

    // Método para convertir Credit a CreditDTO
    private CreditDTO convertToDTO(Credit credit) {
        CreditDTO dto = new CreditDTO();
        dto.setId(credit.getId());
        dto.setCapitalAmount(credit.getCapitalAmount());
        dto.setStartDate(credit.getStartDate());
        dto.setEndDate(credit.getEndDate());
        dto.setGracePeriodDays(credit.getGracePeriodDays());
        dto.setStatus(credit.getStatus());
        return dto;
    }

    public List<Installment> calculatePaymentSchedule(
            Credit credit,
            int numberOfInstallments,
            int gracePeriodDays,
            BigDecimal tea,
            LocalDate firstPaymentDate
    ) {
        // Calcular la tasa mensual a partir de la TEA
        BigDecimal monthlyRate = calculateMonthlyRate(tea);

        // Calcular el interés de gracia
        BigDecimal graceInterest = calculateGraceInterest(credit.getCapitalAmount(), monthlyRate, gracePeriodDays);

        // Añadir el interés de gracia al costo total del préstamo
        BigDecimal totalCost = credit.getCapitalAmount().add(graceInterest);

        // Calcular el monto de la cuota constante (amortización francesa) sobre el costo total
        BigDecimal installmentAmount = calculateConstantInstallment(totalCost, monthlyRate, numberOfInstallments);

        // Establecer la fecha de la primera cuota
        LocalDate dueDate = firstPaymentDate;

        BigDecimal remainingCapital = totalCost;

        List<Installment> installments = new ArrayList<>();

        for (int i = 0; i < numberOfInstallments; i++) {
            Installment installment = new Installment();

            // Calcular el interés de la cuota
            BigDecimal interestAmount = remainingCapital.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            // Calcular el capital amortizado
            BigDecimal capitalPayment = installmentAmount.subtract(interestAmount);

            // Ajustar la última cuota para asegurar que el capital restante se amortice completamente
            if (i == numberOfInstallments - 1) {
                capitalPayment = remainingCapital; // Asegurar que el capital restante se amortice completamente
            }

            // Verificar que el interés no sea negativo
            if (interestAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("El interés no puede ser negativo. Revise los cálculos.");
            }

            // Actualizar el capital restante
            remainingCapital = remainingCapital.subtract(capitalPayment);

            // Configurar la cuota con la fecha fija de vencimiento
            installment.setDueDate(calculateDueDate(dueDate, i)); // Fecha fija de pago
            installment.setAmount(installmentAmount); // Monto de la cuota (constante)
            installment.setCapitalAmount(capitalPayment); // Monto del capital
            installment.setInterestAmount(interestAmount); // Monto del interés
            installment.setCredit(credit); // Asignar el crédito a la cuota

            // Agregar la cuota a la lista
            installments.add(installment);

            // Ajustar la fecha para la siguiente cuota
            dueDate = dueDate.plusMonths(1); // Avanzamos a la siguiente fecha del mes para la próxima cuota
        }

        return installments;
    }
}