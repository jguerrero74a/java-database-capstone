package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "admins") // Define el nombre de la tabla en la base de datos
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username no puede ser nulo")
    private String username;

    @NotNull(message = "password no puede ser nulo")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Constructor vacío requerido por JPA
    public Admin() {
    }

    // Constructor con parámetros (opcional, útil para pruebas)
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}