package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.PaymentLoanDTO;
import com.prestamo.dalp.mapper.PaymentLoanMapper;
import com.prestamo.dalp.model.PaymentLoan;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.PaymentLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/loans")
@Tag(name = "Pagos de Préstamos", description = "Operaciones relacionadas con pagos de préstamos")
public class PaymentLoanController {

    @Autowired
    private PaymentLoanService paymentLoanService;

    @PostMapping("/{loanId}")
    @Operation(summary = "Realizar un pago de préstamo", description = "Procesa un pago para un préstamo específico")
    @ApiResponse(responseCode = "200", description = "Pago realizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<PaymentLoanDTO>> makeLoanPayment(@PathVariable Long loanId, @RequestBody PaymentLoanDTO paymentLoanDTO) {
        try {
            PaymentLoan paymentLoan = PaymentLoanMapper.toEntity(paymentLoanDTO);
            String message = paymentLoanService.makePayment(loanId, paymentLoan);
            List<PaymentLoan> payments = paymentLoanService.getPaymentsByLoanId(loanId);
            PaymentLoan savedPayment = payments.get(payments.size() - 1);
            PaymentLoanDTO responseDTO = PaymentLoanMapper.toDTO(savedPayment);
            CustomApiResponse<PaymentLoanDTO> response = new CustomApiResponse<>(200, message, responseDTO, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<PaymentLoanDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("loanId", "Préstamo no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Obtener pagos por préstamo", description = "Obtiene todos los pagos asociados a un préstamo")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<PaymentLoanDTO>>> getPaymentsByLoanId(@PathVariable Long loanId) {
        try {
            List<PaymentLoan> payments = paymentLoanService.getPaymentsByLoanId(loanId);
            List<PaymentLoanDTO> dtos = payments.stream()
                    .map(PaymentLoanMapper::toDTO)
                    .collect(Collectors.toList());
            CustomApiResponse<List<PaymentLoanDTO>> response = new CustomApiResponse<>(200, "Lista de pagos obtenida con éxito", dtos, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<PaymentLoanDTO>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("loanId", "Préstamo no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }
}