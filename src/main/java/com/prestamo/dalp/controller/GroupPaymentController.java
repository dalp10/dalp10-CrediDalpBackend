package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.GroupPaymentDTO;
import com.prestamo.dalp.mapper.GroupPaymentMapper;
import com.prestamo.dalp.model.GroupPayment;
import com.prestamo.dalp.model.GroupPaymentContribution;
import com.prestamo.dalp.service.GroupPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-payments")
public class GroupPaymentController {

    @Autowired
    private GroupPaymentService groupPaymentService;

    @Autowired
    private GroupPaymentMapper groupPaymentMapper;

    /**
     * Crea un nuevo pago grupal.
     * Antes de crear, se asigna el objeto GroupPayment padre a cada contribución.
     *
     * Ejemplo de JSON:
     * {
     *   "serviceType": "LUZ",
     *   "description": "Pago de luz de febrero",
     *   "totalAmount": 150.00,
     *   "paymentDate": "2023-03-05T10:30:00",
     *   "paymentMethod": "TRANSFERENCIA",
     *   "status": "PENDING",
     *   "payer": { "id": 1 },
     *   "contributions": [
     *     { "client": { "id": 2 }, "amountPaid": 50.00, "contributionDate": "2023-03-06T12:00:00" },
     *     { "client": { "id": 3 }, "amountPaid": 50.00, "contributionDate": "2023-03-06T12:05:00" },
     *     { "client": { "id": 4 }, "amountPaid": 50.00, "contributionDate": "2023-03-06T12:10:00" }
     *   ]
     * }
     *
     * @param dto Objeto GroupPayment a crear.
     * @return El GroupPayment creado.
     */
    @PostMapping
    public ResponseEntity<GroupPaymentDTO> createGroupPayment(@RequestBody GroupPaymentDTO dto) {
        // El servicio se encarga de mapear el payerId a Client y de asignar el objeto padre en las contribuciones.
        GroupPayment savedPayment = groupPaymentService.createGroupPayment(dto);
        GroupPaymentDTO responseDTO = groupPaymentMapper.toDTO(savedPayment);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Obtiene todos los pagos grupales.
     *
     * @return Lista de GroupPayment.
     */
    @GetMapping
    public ResponseEntity<List<GroupPaymentDTO>> getAllGroupPayments() {
        List<GroupPayment> payments = groupPaymentService.getAllGroupPayments();
        List<GroupPaymentDTO> dtos = payments.stream()
                .map(groupPaymentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    /**
     * Obtiene un pago grupal por su ID, incluyendo las contribuciones.
     *
     * @param id ID del pago grupal.
     * @return El GroupPayment encontrado o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupPaymentDTO> getGroupPaymentById(@PathVariable Long id) {
        GroupPayment payment = groupPaymentService.getGroupPaymentById(id);
        if (payment != null) {
            GroupPaymentDTO dto = groupPaymentMapper.toDTO(payment);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene las contribuciones de un pago grupal específico.
     *
     * @param id ID del pago grupal.
     * @return Lista de GroupPaymentContribution o 404 si no existe el pago grupal.
     */
    @GetMapping("/{id}/contributions")
    public ResponseEntity<List<GroupPaymentContribution>> getContributionsByGroupPayment(@PathVariable Long id) {
        GroupPayment payment = groupPaymentService.getGroupPaymentById(id);
        if (payment != null) {
            List<GroupPaymentContribution> contributions = payment.getContributions();
            return ResponseEntity.ok(contributions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza un pago grupal existente.
     *
     * @param id             ID del pago grupal a actualizar.
     * @param updatedPaymentDTO Objeto GroupPayment con los nuevos datos.
     * @return El GroupPayment actualizado o error 404 si no se encuentra.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupPayment(@PathVariable Long id, @RequestBody GroupPaymentDTO updatedPaymentDTO) {
        GroupPayment payment = groupPaymentService.updateGroupPayment(id, updatedPaymentDTO);
        if (payment != null) {
            GroupPaymentDTO responseDTO = groupPaymentMapper.toDTO(payment);
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GroupPayment not found");
        }
    }



    /**
     * Elimina un pago grupal por su ID.
     *
     * @param id ID del pago grupal a eliminar.
     * @return Mensaje de confirmación o error 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupPayment(@PathVariable Long id) {
        boolean deleted = groupPaymentService.deleteGroupPayment(id);
        if (deleted) {
            return ResponseEntity.ok("GroupPayment deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GroupPayment not found");
        }
    }
}
