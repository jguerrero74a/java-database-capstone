package com.project.back_end.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

public class Login {

    @JsonProperty("email")
    @JsonAlias({"username", "adminUsername", "doctorEmail", "identifier"})
    private String email;

    @JsonProperty("password")
    @JsonAlias({"adminPassword", "doctorPassword"})
    private String password;

    public Login() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Métodos de compatibilidad manual para asegurar el éxito del mapeo
    public void setAdminUsername(String adminUsername) { this.email = adminUsername; }
    public void setAdminPassword(String adminPassword) { this.password = adminPassword; }
    public void setUsername(String username) { this.email = username; }
    
    // Método que utiliza el Service para obtener el usuario
    public String getIdentifier() { return email; }
}