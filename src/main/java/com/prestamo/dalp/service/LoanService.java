package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.LoanDTO;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.repository.LoanRepository;
import com.prestamo.dalp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Obtener todos los préstamos
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Obtener un préstamo por ID
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // Crear un nuevo préstamo
    public String createLoan(Loan loan) {
        // Verificar si el cliente existe
        Optional<Client> client = clientRepository.findById(loan.getClient().getId());
        if (client.isPresent()) {
            loan.setClient(client.get());  // Asociar cliente al préstamo
            loanRepository.save(loan);  // El cálculo de los montos se realiza automáticamente
            return "Préstamo creado con éxito";  // Mensaje de éxito
        } else {
            return "Error: Cliente no encontrado";  // Mensaje de error si no existe el cliente
        }
    }

    // Actualizar un préstamo existente
    public String updateLoan(Long id, Loan updatedLoan) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (existingLoan.isPresent()) {
            Loan loan = existingLoan.get();
            loan.setAmount(updatedLoan.getAmount());
            loan.setInterestRate(updatedLoan.getInterestRate());
            loan.setIssueDate(updatedLoan.getIssueDate());
            loan.setDueDate(updatedLoan.getDueDate());
            loan.setClient(updatedLoan.getClient());

            // No es necesario llamar a calculateAmounts() aquí, ya que se maneja en la entidad Loan
            loanRepository.save(loan);
            return "Préstamo actualizado con éxito";  // Mensaje de éxito
        } else {
            return "Error: Préstamo no encontrado";  // Mensaje de error si no existe el préstamo
        }
    }

    // Eliminar un préstamo
    public String deleteLoan(Long id) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (existingLoan.isPresent()) {
            loanRepository.deleteById(id);
            return "Préstamo eliminado con éxito";  // Mensaje de éxito
        } else {
            return "Error: Préstamo no encontrado";  // Mensaje de error si no existe el préstamo
        }
    }

    public LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setAmount(loan.getAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setIssueDate(loan.getIssueDate());
        dto.setDueDate(loan.getDueDate());
        dto.setLoanCode(loan.getLoanCode());
        dto.setInterestAmount(loan.getInterestAmount());
        dto.setTotalAmount(loan.getTotalAmount());
        dto.setStatus(loan.getStatus());
        dto.setClientId(loan.getClient() != null ? loan.getClient().getId() : null);
        return dto;
    }
}
