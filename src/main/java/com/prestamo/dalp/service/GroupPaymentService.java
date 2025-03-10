package com.prestamo.dalp.service;

import com.prestamo.dalp.mapper.GroupPaymentMapper;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.GroupPayment;
import com.prestamo.dalp.model.GroupPaymentContribution;
import com.prestamo.dalp.repository.ClientRepository;
import com.prestamo.dalp.repository.GroupPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.prestamo.dalp.DTO.GroupPaymentDTO;
import com.prestamo.dalp.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;


/**
 * Servicio para gestionar pagos grupales.
 */
@Service
public class GroupPaymentService {

    @Autowired
    private GroupPaymentRepository groupPaymentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private GroupPaymentMapper groupPaymentMapper;
    /**
     * Crea un nuevo pago grupal.
     * Antes de guardar, asigna el GroupPayment padre a cada contribución.
     *
     * @param dto Objeto GroupPayment a crear.
     * @return El GroupPayment creado.
     */
    public GroupPayment createGroupPayment(GroupPaymentDTO dto) {
        GroupPayment groupPayment = new GroupPayment();
        groupPayment.setServiceType(dto.getServiceType());
        groupPayment.setDescription(dto.getDescription());
        groupPayment.setTotalAmount(dto.getTotalAmount());
        groupPayment.setPaymentDate(dto.getPaymentDate());
        groupPayment.setPaymentMethod(dto.getPaymentMethod());
        groupPayment.setStatus(dto.getStatus());
        groupPayment.setReimbursedAmount(dto.getReimbursedAmount());
        groupPayment.setPendingReimbursement(dto.getPendingReimbursement());

        // Convertir payerId a objeto Client
        if (dto.getPayerId() != null) {
            Optional<Client> payerOpt = clientRepository.findById(dto.getPayerId());
            if (payerOpt.isPresent()) {
                groupPayment.setPayer(payerOpt.get());
            } else {
                throw new IllegalArgumentException("El cliente con ID " + dto.getPayerId() + " no existe");
            }
        }

        // Asignar contribuciones si existen (y asegurarse de que cada contribución tenga asignado este GroupPayment)
        if (dto.getContributions() != null && !dto.getContributions().isEmpty()) {
            dto.getContributions().forEach(contribDTO -> {
                GroupPaymentContribution contrib = new GroupPaymentContribution();
                // Aquí deberás mapear los campos de contribDTO a la entidad y buscar el cliente si es necesario
                contrib.setAmountPaid(contribDTO.getAmountPaid());
                contrib.setContributionDate(contribDTO.getContributionDate());
                // Asumimos que el método de pago se puede convertir
                contrib.setPaymentMethod(PaymentMethod.valueOf(contribDTO.getPaymentMethod()));
                // Buscar y asignar el cliente de la contribución
                Optional<Client> clientOpt = clientRepository.findById(contribDTO.getClientId());
                if (clientOpt.isPresent()) {
                    contrib.setClient(clientOpt.get());
                } else {
                    throw new IllegalArgumentException("El cliente con ID " + contribDTO.getClientId() + " no existe");
                }
                contrib.setGroupPayment(groupPayment);
                groupPayment.getContributions().add(contrib);
            });
        }
        return groupPaymentRepository.save(groupPayment);
    }


    /**
     * Actualiza un pago grupal existente.
     *
     * @param id             ID del pago grupal a actualizar.
     * @param updatedPaymentDTO Objeto GroupPayment con los nuevos datos.
     * @return El GroupPayment actualizado, o null si no se encontró.
     */
    public GroupPayment updateGroupPayment(Long id, GroupPaymentDTO updatedPaymentDTO) {
        Optional<GroupPayment> optionalPayment = groupPaymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            GroupPayment existingPayment = optionalPayment.get();

            // Actualizar campos básicos
            existingPayment.setServiceType(updatedPaymentDTO.getServiceType());
            existingPayment.setDescription(updatedPaymentDTO.getDescription());
            existingPayment.setTotalAmount(updatedPaymentDTO.getTotalAmount());
            existingPayment.setPaymentDate(updatedPaymentDTO.getPaymentDate());
            existingPayment.setPaymentMethod(updatedPaymentDTO.getPaymentMethod());

            // Actualizar el pagador
            if (updatedPaymentDTO.getPayerId() != null) {
                Optional<Client> payerOpt = clientRepository.findById(updatedPaymentDTO.getPayerId());
                payerOpt.ifPresent(existingPayment::setPayer);
            }

            // Actualizar contribuciones
            if (updatedPaymentDTO.getContributions() != null) {
                List<GroupPaymentContribution> nuevaListaDeContribuciones = updatedPaymentDTO.getContributions().stream()
                        .map(dto -> {
                            GroupPaymentContribution contribution = groupPaymentMapper.toEntity(dto);
                            if (contribution.getId() == null || contribution.getId() == 0) {
                                contribution.setId(null); // Para que se trate como una nueva contribución
                            }
                            contribution.setGroupPayment(existingPayment);
                            return contribution;
                        })
                        .toList();

                existingPayment.getContributions().clear();
                existingPayment.getContributions().addAll(nuevaListaDeContribuciones);
            }

            // **Actualizar montos después de modificar las contribuciones**
            BigDecimal totalReembolsado = existingPayment.getContributions().stream()
                    .map(GroupPaymentContribution::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            existingPayment.setReimbursedAmount(totalReembolsado);
            existingPayment.setPendingReimbursement(existingPayment.getTotalAmount().subtract(totalReembolsado));

            // **Actualizar el estado automáticamente**
            if (totalReembolsado.compareTo(existingPayment.getTotalAmount()) >= 0) {
                existingPayment.setStatus("COMPLETED"); // Cambio de estado cuando está totalmente pagado
            } else {
                existingPayment.setStatus("PENDING"); // Estado sigue pendiente si no se ha pagado todo
            }

            return groupPaymentRepository.save(existingPayment);
        } else {
            return null;
        }
    }






    private void updateExistingPaymentFields(GroupPayment existingPayment, GroupPayment updatedPayment) {
        existingPayment.setServiceType(updatedPayment.getServiceType());
        existingPayment.setDescription(updatedPayment.getDescription());
        existingPayment.setTotalAmount(updatedPayment.getTotalAmount());
        existingPayment.setPaymentDate(updatedPayment.getPaymentDate());
        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
        existingPayment.setStatus(updatedPayment.getStatus());
        existingPayment.setPayer(updatedPayment.getPayer());
        // Aquí puedes actualizar otros campos si es necesario
    }




    /**
     * Elimina un pago grupal por su ID.
     *
     * @param id ID del pago grupal a eliminar.
     * @return true si se eliminó, false en caso contrario.
     */
    public boolean deleteGroupPayment(Long id) {
        if (groupPaymentRepository.existsById(id)) {
            groupPaymentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Obtiene un pago grupal por su ID.
     *
     * @param id ID del pago grupal.
     * @return El GroupPayment encontrado o null si no existe.
     */
    public GroupPayment getGroupPaymentById(Long id) {
        return groupPaymentRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene todos los pagos grupales.
     *
     * @return Lista de GroupPayment.
     */
    public List<GroupPayment> getAllGroupPayments() {
        return groupPaymentRepository.findAll();
    }
}
