package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Método personalizado para buscar un cliente por número de documento
    Optional<Client> findByDocumentNumber(String documentNumber);

    // Método personalizado para buscar un cliente por email (opcional, si necesitas)
    Optional<Client> findByEmail(String email);

    // Buscar clientes por número de documento o nombre
    @Query("SELECT c FROM Client c WHERE c.documentNumber LIKE %:query% OR c.firstName LIKE %:query% OR c.lastName LIKE %:query%")
    List<Client> findByDocumentNumberOrNameContaining(@Param("query") String query);

    // Puedes agregar más consultas personalizadas si las necesitas en el futuro
}
