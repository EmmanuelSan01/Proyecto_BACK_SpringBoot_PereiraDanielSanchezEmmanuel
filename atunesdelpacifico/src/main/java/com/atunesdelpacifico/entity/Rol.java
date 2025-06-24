package com.atunesdelpacifico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Byte idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Rol() {}

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters and Setters
    public Byte getIdRol() { return idRol; }
    public void setIdRol(Byte idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
}
