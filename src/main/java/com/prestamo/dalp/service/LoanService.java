package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.LoanDTO;
import com.prestamo.dalp.mapper.LoanMapper;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.LoanHistory;
import com.prestamo.dalp.repository.LoanHistoryRepository;
import com.prestamo.dalp.repository.LoanRepository;
import com.prestamo.dalp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanHistoryRepository loanHistoryRepository;

    /**
     * Obtiene todos los préstamos registrados en la base de datos.
     *
     * @return Lista de préstamos en formato DTO.
     */
    public List<LoanDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(LoanMapper::toDTO) // Convertir cada entidad Loan a LoanDTO
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un préstamo por su ID.
     *
     * @param id ID del préstamo a buscar.
     * @return El préstamo en formato DTO, o null si no se encuentra.
     */
    public LoanDTO getLoanById(Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        return loan.map(LoanMapper::toDTO).orElse(null); // Convertir la entidad a DTO si existe
    }

    /**
     * Crea un nuevo préstamo y lo guarda en la base de datos.
     *
     * @param loanDTO Datos del préstamo en formato DTO.
     * @return El préstamo creado en formato DTO.
     * @throws RuntimeException Si el cliente asociado no existe.
     */
    public LoanDTO createLoan(LoanDTO loanDTO) {
        // Verificar si el cliente existe
        Optional<Client> client = clientRepository.findById(loanDTO.getClientId());
        if (client.isEmpty()) {
            throw new RuntimeException("Error: Cliente no encontrado");
        }

        // Verificar si el loan_code ya existe
        Optional<Loan> existingLoan = loanRepository.findByLoanCode(loanDTO.getLoanCode());
        if (existingLoan.isPresent()) {
            throw new RuntimeException("Error: El código del préstamo ya existe");
        }

        // Convertir el DTO a entidad
        Loan loan = LoanMapper.toEntity(loanDTO);
        loan.setClient(client.get());  // Asociar cliente al préstamo

        // Inicializar capital_PAGADO e interest_PAGADO en cero si no se proporcionan
        if (loanDTO.getCapitalPaid() == null) {
            loan.setCapitalPaid(BigDecimal.ZERO);
        }
        if (loanDTO.getInterestPaid() == null) {
            loan.setInterestPaid(BigDecimal.ZERO);
        }

        // Calcular remainingCapital y remainingInterest si no se enviaron
        if (loanDTO.getRemainingCapital() == null) {
            loan.setRemainingCapital(loan.getAmount()); // Al crear el préstamo, el capital restante es igual al monto total
        }
        if (loanDTO.getRemainingInterest() == null) {
            // Calcular el interés restante: amount * (interestRate / 100)
            BigDecimal interestRatePercentage = loan.getInterestRate().divide(BigDecimal.valueOf(100));
            BigDecimal remainingInterest = loan.getAmount().multiply(interestRatePercentage);
            loan.setRemainingInterest(remainingInterest);
        }

        // Calcular días de atraso (days_VENCIDO)
        if (loanDTO.getDueDate() != null && loanDTO.getIssueDate() != null) {
            long daysVENCIDO = ChronoUnit.DAYS.between(loanDTO.getIssueDate(), loanDTO.getDueDate());
            loan.setDaysOverdue((int) daysVENCIDO);
        }

        // Guardar el préstamo
        loanRepository.save(loan);

        // Crear registro de historial inicial
        LoanHistory history = new LoanHistory();
        history.setLoan(loan);
        history.setTotalAmount(loan.getTotalAmount());
        history.setAmount(loan.getAmount());
        history.setInterestAmount(loan.getInterestAmount());
        history.setCapitalPaid(loan.getCapitalPaid());
        history.setInterestPaid(loan.getInterestPaid());
        history.setTimestamp(LocalDateTime.now());
        loanHistoryRepository.save(history);

        // Convertir la entidad guardada a DTO para devolverla en la respuesta
        return LoanMapper.toDTO(loan);
    }

    /**
     * Actualiza un préstamo existente.
     *
     * @param id ID del préstamo a actualizar.
     * @param updatedLoanDTO Datos actualizados del préstamo en formato DTO.
     * @return El préstamo actualizado en formato DTO.
     * @throws RuntimeException Si el préstamo no se encuentra.
     */
    public LoanDTO updateLoan(Long id, LoanDTO updatedLoanDTO) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (existingLoan.isPresent()) {
            Loan loan = existingLoan.get();
            loan.setAmount(updatedLoanDTO.getAmount());
            loan.setInterestRate(updatedLoanDTO.getInterestRate());
            loan.setIssueDate(updatedLoanDTO.getIssueDate());
            loan.setDueDate(updatedLoanDTO.getDueDate());
            loan.setStatus(updatedLoanDTO.getStatus());
            loan.setRemainingCapital(updatedLoanDTO.getRemainingCapital());
            loan.setRemainingInterest(updatedLoanDTO.getRemainingInterest());

            // Asignar el cliente si el clientId está presente
            if (updatedLoanDTO.getClientId() != null) {
                Optional<Client> client = clientRepository.findById(updatedLoanDTO.getClientId());
                client.ifPresent(loan::setClient);
            }

            // Guardar los cambios en la base de datos
            loanRepository.save(loan);

            // Convertir la entidad actualizada a DTO y devolverla
            return LoanMapper.toDTO(loan);
        } else {
            throw new RuntimeException("Error: Préstamo no encontrado");
        }
    }

    /**
     * Elimina un préstamo por su ID.
     *
     * @param id ID del préstamo a eliminar.
     * @return Mensaje de éxito o error.
     */
    public String deleteLoan(Long id) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (existingLoan.isPresent()) {
            loanRepository.deleteById(id);
            return "Préstamo eliminado con éxito";  // Mensaje de éxito
        } else {
            return "Error: Préstamo no encontrado";  // Mensaje de error si no existe el préstamo
        }
    }

    /**
     * Obtiene todos los préstamos asociados a un cliente.
     *
     * @param clientId ID del cliente.
     * @return Lista de préstamos en formato DTO.
     */
    public List<LoanDTO> getLoansByClient(Long clientId) {
        List<Loan> loans = loanRepository.findByClient_Id(clientId);
        return loans.stream()
                .map(LoanMapper::toDTO) // Convertir cada entidad Loan a LoanDTO
                .collect(Collectors.toList());
    }
}