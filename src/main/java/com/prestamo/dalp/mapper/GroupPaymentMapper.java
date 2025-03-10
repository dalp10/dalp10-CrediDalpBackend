package com.prestamo.dalp.mapper;

import com.prestamo.dalp.DTO.GroupPaymentContributionDTO;
import com.prestamo.dalp.DTO.GroupPaymentDTO;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.GroupPayment;
import com.prestamo.dalp.model.GroupPaymentContribution;
import com.prestamo.dalp.model.PaymentMethod;
import com.prestamo.dalp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GroupPaymentMapper {

    @Autowired
    private ClientRepository clientRepository;

    public GroupPaymentContribution toEntity(GroupPaymentContributionDTO dto) {
        GroupPaymentContribution contribution = new GroupPaymentContribution();
        contribution.setId(dto.getId());
        contribution.setAmountPaid(dto.getAmountPaid());
        contribution.setContributionDate(dto.getContributionDate());
        contribution.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));

        Optional<Client> clientOpt = clientRepository.findById(dto.getClientId());
        if (!clientOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente con ID " + dto.getClientId() + " no existe");
        }
        contribution.setClient(clientOpt.get());

        return contribution;
    }

    public GroupPaymentDTO toDTO(GroupPayment groupPayment) {
        GroupPaymentDTO dto = new GroupPaymentDTO();
        dto.setId(groupPayment.getId());
        dto.setServiceType(groupPayment.getServiceType());
        dto.setDescription(groupPayment.getDescription());
        dto.setTotalAmount(groupPayment.getTotalAmount());
        dto.setPaymentDate(groupPayment.getPaymentDate());
        dto.setPaymentMethod(groupPayment.getPaymentMethod());
        dto.setStatus(groupPayment.getStatus());
        dto.setReimbursedAmount(groupPayment.getReimbursedAmount());
        dto.setPendingReimbursement(groupPayment.getPendingReimbursement());
        dto.setPayerId(groupPayment.getPayer() != null ? groupPayment.getPayer().getId() : null);

        if (groupPayment.getContributions() != null) {
            List<GroupPaymentContributionDTO> contributionsDTO = groupPayment.getContributions().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            dto.setContributions(contributionsDTO);
        }
        return dto;
    }

    public GroupPaymentContributionDTO toDTO(GroupPaymentContribution contribution) {
        GroupPaymentContributionDTO dto = new GroupPaymentContributionDTO();
        dto.setId(contribution.getId());
        dto.setClientId(contribution.getClient() != null ? contribution.getClient().getId() : null);
        dto.setAmountPaid(contribution.getAmountPaid());
        dto.setContributionDate(contribution.getContributionDate());
        dto.setPaymentMethod(contribution.getPaymentMethod().name());
        return dto;
    }

    // Métodos fromDTO (opcional) para convertir de DTO a entidad
}
