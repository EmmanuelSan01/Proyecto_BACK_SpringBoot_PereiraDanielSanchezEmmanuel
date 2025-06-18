package com.atunesdelpacifico.model.dto;

import com.atunesdelpacifico.entity.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

public class ClienteRequest {

    @NotNull(message = "El tipo de cliente es obligatorio")
    private Cliente.TipoCliente tipo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 50, message = "La identificación no puede exceder 50 caracteres")
    private String identificacion;

    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    // Usuario data
    @Valid
    @NotNull(message = "Los datos del usuario son obligatorios")
    private UsuarioRequest usuario;

    // Constructors
    public ClienteRequest() {}

    // Getters and Setters
    public Cliente.TipoCliente getTipo() { return tipo; }
    public void setTipo(Cliente.TipoCliente tipo) { this.tipo = tipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public UsuarioRequest getUsuario() { return usuario; }
    public void setUsuario(UsuarioRequest usuario) { this.usuario = usuario; }
}
