package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Login DTO: Clase para representar los datos de la solicitud de inicio de sesión.
 * Se utiliza para recibir las credenciales del cliente mediante @RequestBody.
 */
public class Login {

    @JsonProperty("email")
    private String identifier;
    
    private String password;

    // Constructor por defecto
    public Login() {
    }

    /**
     * Constructor con parámetros para inicialización rápida.
     */
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters and Setters

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}