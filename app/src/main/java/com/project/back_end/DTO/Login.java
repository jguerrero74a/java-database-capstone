package com.project.back_end.DTO;

/**
 * Login DTO: Clase para representar los datos de la solicitud de inicio de sesión.
 * Se utiliza para recibir las credenciales del cliente mediante @RequestBody.
 */
public class Login {

    private String identifier;
    private String password;

    // 3. Constructor: Se utiliza el constructor por defecto (implícito)
    public Login() {
    }

    /**
     * Constructor con parámetros para inicialización rápida.
     */
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // 4. Getters and Setters: Estándar para habilitar la deserialización JSON

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