package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.PaymentCreditDTO;
import com.prestamo.dalp.mapper.PaymentCreditMapper;
import com.prestamo.dalp.model.PaymentCredit;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.PaymentCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/credits")
@Tag(name = "Pagos de Créditos", description = "Operaciones relacionadas con pagos de créditos")
public class PaymentCreditController {

    @Autowired
    private PaymentCreditService paymentCreditService;

    @PostMapping
    @Operation(summary = "Realizar un pago de crédito", description = "Procesa un pago para un crédito específico")
    @ApiResponse(responseCode = "200", description = "Pago realizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<PaymentCreditDTO>> makeCreditPayment(@RequestBody PaymentCreditDTO paymentCreditDTO) {
        try {
            PaymentCredit paymentCredit = PaymentCreditMapper.toEntity(paymentCreditDTO);
            PaymentCredit savedPayment = paymentCreditService.makePayment(
                    paymentCreditDTO.getCreditId(),
                    paymentCredit.getInstallmentNumber(),
                    paymentCredit.getInterestPaid(),
                    paymentCredit.getCapitalPaid(),
                    paymentCredit.getTotalPaid(),
                    paymentCredit.getPaymentDate(),
                    paymentCreditDTO.getPaymentMethod().name()
            );
            PaymentCreditDTO responseDTO = PaymentCreditMapper.toDTO(savedPayment);
            CustomApiResponse<PaymentCreditDTO> response = new CustomApiResponse<>(200, "Pago realizado con éxito", responseDTO, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<PaymentCreditDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("payment", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{creditId}")
    @Operation(summary = "Obtener pagos por crédito", description = "Obtiene todos los pagos asociados a un crédito")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<PaymentCreditDTO>>> getPaymentsByCreditId(@PathVariable Long creditId) {
        try {
            List<PaymentCredit> payments = paymentCreditService.getPaymentsByCreditId(creditId);
            List<PaymentCreditDTO> dtos = payments.stream()
                    .map(PaymentCreditMapper::toDTO)
                    .collect(Collectors.toList());
            CustomApiResponse<List<PaymentCreditDTO>> response = new CustomApiResponse<>(200, "Lista de pagos obtenida con éxito", dtos, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<PaymentCreditDTO>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("creditId", "Crédito no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }
}