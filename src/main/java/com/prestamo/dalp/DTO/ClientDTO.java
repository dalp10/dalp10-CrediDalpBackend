package com.prestamo.dalp.DTO;

import com.prestamo.dalp.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String documentNumber;
    private String clientIdentifier; // Campo agregado
    private boolean hasCredits;

    // Constructor personalizado que acepta un objeto Client y un booleano hasCredits
    public ClientDTO(Client client, boolean hasCredits) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.phone = client.getPhone();
        this.documentNumber = client.getDocumentNumber();
        this.clientIdentifier = client.getClientIdentifier(); // Mapea el clientIdentifier
        this.hasCredits = hasCredits;
    }
}