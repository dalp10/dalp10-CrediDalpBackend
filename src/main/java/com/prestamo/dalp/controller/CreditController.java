package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.CreditDTO;
import com.prestamo.dalp.DTO.InstallmentDTO;
import com.prestamo.dalp.DTO.PaymentCreditDTO;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.CreditService;
import com.prestamo.dalp.service.InstallmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
@Tag(name = "Créditos", description = "Operaciones relacionadas con créditos")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private InstallmentService installmentService;

    @PostMapping
    @Operation(summary = "Crear un nuevo crédito", description = "Crea un nuevo crédito y genera su plan de pagos")
    @ApiResponse(responseCode = "200", description = "Crédito creado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<Credit>> createCredit(
            @RequestBody Credit credit,
            @RequestParam int numberOfInstallments,
            @RequestParam int gracePeriodDays,
            @RequestParam BigDecimal tea,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate firstPaymentDate
    ) {
        try {
            Credit createdCredit = creditService.createCredit(credit, numberOfInstallments, gracePeriodDays, tea, firstPaymentDate);
            CustomApiResponse<Credit> response = new CustomApiResponse<>(200, "Crédito creado con éxito", createdCredit, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<Credit> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("credit", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener créditos por cliente", description = "Obtiene todos los créditos asociados a un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de créditos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<CreditDTO>>> getCreditsByClient(@PathVariable Long clientId) {
        try {
            List<CreditDTO> credits = creditService.getCreditsByClient(clientId);
            CustomApiResponse<List<CreditDTO>> response = new CustomApiResponse<>(200, "Lista de créditos obtenida con éxito", credits, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<CreditDTO>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("clientId", "Cliente no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/installments/{installmentId}/pay")
    @Operation(summary = "Pagar una cuota", description = "Procesa el pago de una cuota específica de un crédito")
    @ApiResponse(responseCode = "200", description = "Cuota pagada con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<Installment>> payInstallment(@PathVariable Long installmentId, @RequestBody PaymentCreditDTO paymentCreditDTO) {
        try {
            Installment updatedInstallment = installmentService.processPayment(paymentCreditDTO);
            creditService.updateCreditStatus(updatedInstallment.getCredit().getId());
            CustomApiResponse<Installment> response = new CustomApiResponse<>(200, "Cuota pagada con éxito", updatedInstallment, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<Installment> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("installmentId", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/calculate-payment-schedule")
    @Operation(summary = "Calcular calendario de pagos", description = "Calcula el calendario de pagos para un crédito")
    @ApiResponse(responseCode = "200", description = "Calendario de pagos calculado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<Installment>>> calculatePaymentSchedule(
            @RequestBody Credit credit,
            @RequestParam int numberOfInstallments,
            @RequestParam int gracePeriodDays,
            @RequestParam BigDecimal tea,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate firstPaymentDate
    ) {
        try {
            List<Installment> installments = creditService.calculatePaymentSchedule(credit, numberOfInstallments, gracePeriodDays, tea, firstPaymentDate);
            CustomApiResponse<List<Installment>> response = new CustomApiResponse<>(200, "Calendario de pagos calculado con éxito", installments, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<Installment>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("credit", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{creditId}/installments")
    @Operation(summary = "Obtener cuotas por crédito", description = "Obtiene las cuotas de un crédito por su ID")
    @ApiResponse(responseCode = "200", description = "Lista de cuotas obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<InstallmentDTO>>> getInstallmentsByCreditId(@PathVariable Long creditId) {
        try {
            List<InstallmentDTO> installments = installmentService.getInstallmentsByCreditId(creditId);
            CustomApiResponse<List<InstallmentDTO>> response = new CustomApiResponse<>(200, "Lista de cuotas obtenida con éxito", installments, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<InstallmentDTO>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("creditId", "Crédito no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }
}