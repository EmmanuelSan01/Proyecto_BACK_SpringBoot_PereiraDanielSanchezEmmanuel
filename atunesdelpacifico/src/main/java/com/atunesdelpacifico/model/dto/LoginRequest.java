package com.atunesdelpacifico.model.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    // Getters and Setters
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    // Métodos adicionales para compatibilidad
    public String getUsernameOrEmail() { return nombreUsuario; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.nombreUsuario = usernameOrEmail; }

    public String getPassword() { return contrasena; }
    public void setPassword(String password) { this.contrasena = password; }
}
