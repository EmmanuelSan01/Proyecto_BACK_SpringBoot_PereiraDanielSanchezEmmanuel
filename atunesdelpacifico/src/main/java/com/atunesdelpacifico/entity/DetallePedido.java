package com.atunesdelpacifico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede ser mayor a 100%")
    @Column(name = "descuento_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPct = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El descuento en valor no puede ser negativo")
    @Column(name = "descuento_valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuentoValor = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calcularSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        calcularSubtotal();
    }

    private void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null && descuentoPct != null) {
            BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            BigDecimal descuentoPorcentaje = total.multiply(descuentoPct).divide(BigDecimal.valueOf(100));
            subtotal = total.subtract(descuentoPorcentaje).subtract(descuentoValor != null ? descuentoValor : BigDecimal.ZERO);
        }
    }

    // Constructors
    public DetallePedido() {}

    public DetallePedido(Pedido pedido, Lote lote, Integer cantidad, BigDecimal precioUnitario, BigDecimal descuentoPct) {
        this.pedido = pedido;
        this.lote = lote;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoPct = descuentoPct;
    }

    // Getters and Setters
    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getDescuentoPct() { return descuentoPct; }
    public void setDescuentoPct(BigDecimal descuentoPct) { this.descuentoPct = descuentoPct; }

    public BigDecimal getDescuentoValor() { return descuentoValor; }
    public void setDescuentoValor(BigDecimal descuentoValor) { this.descuentoValor = descuentoValor; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
