package com.prestamo.dalp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")  // Tabla para los roles
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;  // Nombre del rol, como "ADMIN", "USER", etc.

    // Otros campos según lo que necesites
}
