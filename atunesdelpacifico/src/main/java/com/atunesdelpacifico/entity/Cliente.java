package com.atunesdelpacifico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @Column(name = "id_usuario")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCliente tipo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 50, message = "La identificación no puede exceder 50 caracteres")
    @Column(name = "identificacion", nullable = false, unique = true)
    private String identificacion;

    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    @Column(name = "telefono")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    @Column(name = "direccion")
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums que coinciden con la base de datos
    public enum TipoCliente {
        PERSONA_NATURAL,
        EMPRESA
    }

    public enum EstadoCliente {
        ACTIVO("Activo"),
        INACTIVO("Inactivo"),
        SUSPENDIDO("Suspendido");

        private final String displayName;

        EstadoCliente(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Cliente() {}

    public Cliente(TipoCliente tipo, String nombre, String identificacion, String telefono, String direccion, Usuario usuario) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.telefono = telefono;
        this.direccion = direccion;
        this.usuario = usuario;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
