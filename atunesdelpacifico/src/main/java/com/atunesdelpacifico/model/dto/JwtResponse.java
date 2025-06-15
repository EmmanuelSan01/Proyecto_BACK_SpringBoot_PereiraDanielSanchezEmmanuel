package com.atunesdelpacifico.model.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String nombreUsuario;
    private String correo;
    private String rol;
    
    public JwtResponse(String token, String nombreUsuario, String correo, String rol) {
        this.token = token;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.rol = rol;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
