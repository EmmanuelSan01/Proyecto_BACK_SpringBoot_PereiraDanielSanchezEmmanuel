package com.atunesdelpacifico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @NotBlank(message = "El número de pedido es obligatorio")
    @Size(max = 50, message = "El número de pedido no puede exceder 50 caracteres")
    @Column(name = "numero_pedido", nullable = false, unique = true)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "impuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago = MetodoPago.TRANSFERENCIA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Usuario createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "ultima_actualizacion_automatica")
    private LocalDateTime ultimaActualizacionAutomatica;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums que coinciden con la base de datos
    public enum EstadoPedido {
        PENDIENTE("Pendiente"),
        EN_PROCESO("En Proceso"),
        ENVIADO("Enviado"),
        ENTREGADO("Entregado"),
        CANCELADO("Cancelado");

        private final String displayName;

        EstadoPedido(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum MetodoPago {
        EFECTIVO("Efectivo"),
        TRANSFERENCIA("Transferencia"),
        CREDITO("Crédito");

        private final String displayName;

        MetodoPago(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Pedido() {}

    public Pedido(String numeroPedido, Cliente cliente, LocalDate fechaEntrega) {
        this.numeroPedido = numeroPedido;
        this.cliente = cliente;
        this.fechaEntrega = fechaEntrega;
    }

    // Getters and Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalDate getFechaEntregaReal() { return fechaEntregaReal; }
    public void setFechaEntregaReal(LocalDate fechaEntregaReal) { this.fechaEntregaReal = fechaEntregaReal; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public Usuario getCreatedBy() { return createdBy; }
    public void setCreatedBy(Usuario createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public LocalDateTime getUltimaActualizacionAutomatica() { return ultimaActualizacionAutomatica; }
    public void setUltimaActualizacionAutomatica(LocalDateTime ultimaActualizacionAutomatica) { this.ultimaActualizacionAutomatica = ultimaActualizacionAutomatica; }
}
