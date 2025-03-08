package com.prestamo.dalp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String documentNumber;

    @Column(nullable = false, unique = true, updatable = false)
    private String clientIdentifier;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Credit> credits; // Agrega este campo si no existe

    @PrePersist
    public void generateClientIdentifier() {
        this.clientIdentifier = "CL-" + UUID.randomUUID().toString().substring(0, 8);  // Genera un identificador con el prefijo "CL-" y un UUID corto
    }
}
