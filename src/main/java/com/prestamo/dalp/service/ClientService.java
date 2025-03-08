package com.prestamo.dalp.service;

import com.prestamo.dalp.DTO.ClientDTO;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.repository.ClientRepository;
import com.prestamo.dalp.repository.CreditRepository;
import com.prestamo.dalp.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private LoanRepository loanRepository;

    // Obtener todos los clientes
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Obtener un cliente por ID
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // Crear un nuevo cliente
    public String createClient(Client client) {
        // Validar duplicidad del número de documento
        Optional<Client> existingClientByDoc = clientRepository.findByDocumentNumber(client.getDocumentNumber());
        if (existingClientByDoc.isPresent()) {
            return "Error: El número de documento ya está registrado.";
        }

        // Validar duplicidad del email
        Optional<Client> existingClientByEmail = clientRepository.findByEmail(client.getEmail());
        if (existingClientByEmail.isPresent()) {
            return "Error: El email ya está registrado.";
        }

        // (Opcional) Validar formato de correo Gmail
        if (!client.getEmail().matches("^[\\w\\.-]+@gmail\\.com$")) {
            return "Error: El email debe ser un correo de Gmail válido.";
        }

        // Guardar el nuevo cliente si todas las validaciones pasan
        clientRepository.save(client);
        return "Cliente registrado correctamente.";
    }


    // Actualizar un cliente existente
    public String updateClient(Long id, Client updatedClient) {
        // Verificar si el DNI ya existe en otro cliente distinto del que se va a actualizar
        Optional<Client> clientWithDni = clientRepository.findByDocumentNumber(updatedClient.getDocumentNumber());
        if (clientWithDni.isPresent() && !clientWithDni.get().getId().equals(id)) {
            // Retorna mensaje indicando que el DNI ya existe
            return "Error: El número de documento ya está registrado.";
        }

        Optional<Client> existingClient = clientRepository.findById(id);
        if (existingClient.isPresent()) {
            Client client = existingClient.get();
            client.setFirstName(updatedClient.getFirstName());
            client.setLastName(updatedClient.getLastName());
            client.setEmail(updatedClient.getEmail());
            client.setPhone(updatedClient.getPhone());
            client.setDocumentNumber(updatedClient.getDocumentNumber());
            clientRepository.save(client);
            return "Cliente actualizado correctamente.";
        } else {
            return "Error: Cliente no encontrado.";
        }
    }

    // Eliminar un cliente
    public String deleteClient(Long id) {
        Optional<Client> existingClient = clientRepository.findById(id);
        if (existingClient.isPresent()) {
            clientRepository.deleteById(id);
            return "Cliente eliminado correctamente.";
        } else {
            return "Error: Cliente no encontrado.";
        }
    }

    // Método para verificar si el número de documento ya está registrado
    public boolean isDocumentNumberExists(String documentNumber) {
        Optional<Client> existingClient = clientRepository.findByDocumentNumber(documentNumber);
        return existingClient.isPresent();
    }

    // Método para obtener los créditos de un cliente por su ID
    public List<Credit> getCreditsByClientId(Long clientId) {
        return creditRepository.findByClientId(clientId); // Obtener los créditos del cliente
    }

    // Método para obtener los créditos de un cliente por su ID
    public List<Loan> getLoansByClientId(Long clientId) {
        return loanRepository.findByClient_Id(clientId); // Obtener los créditos del cliente
    }

    public boolean hasCredits(Long clientId) {
        boolean hasCredits = !creditRepository.findByClientId(clientId).isEmpty();
        boolean hasLoans = !loanRepository.findByClient_Id(clientId).isEmpty();
        return hasCredits || hasLoans; // Devuelve true si tiene créditos o préstamos
    }

    // Método para buscar clientes por número de documento o nombre
    public List<ClientDTO> searchClients(String query) {
        List<Client> clients = clientRepository.findByDocumentNumberOrNameContaining(query);
        return clients.stream()
                .map(client -> {
                    boolean hasCredits = hasCredits(client.getId());
                    return new ClientDTO(client, hasCredits);
                })
                .collect(Collectors.toList());
    }
}
