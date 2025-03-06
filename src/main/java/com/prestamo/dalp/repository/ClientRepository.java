package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Método personalizado para buscar un cliente por número de documento
    Optional<Client> findByDocumentNumber(String documentNumber);

    // Método personalizado para buscar un cliente por email (opcional, si necesitas)
    Optional<Client> findByEmail(String email);

    // Puedes agregar más consultas personalizadas si las necesitas en el futuro
}
