package com.atunesdelpacifico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @NotBlank(message = "El código de lote es obligatorio")
    @Size(max = 50, message = "El código de lote no puede exceder 50 caracteres")
    @Column(name = "codigo_lote", nullable = false, unique = true)
    private String codigoLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "fecha_prod", nullable = false)
    private LocalDate fechaProd;

    @Column(name = "fecha_venc", nullable = false)
    private LocalDate fechaVenc;

    @Min(value = 0, message = "La cantidad total no puede ser negativa")
    @Column(name = "cantidad_total", nullable = false)
    private Integer cantidadTotal;

    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Column(name = "cantidad_disp", nullable = false)
    private Integer cantidadDisp;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoLote estado = EstadoLote.DISPONIBLE;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(name = "ubicacion")
    private String ubicacion;

    @Size(max = 100, message = "El lote del proveedor no puede exceder 100 caracteres")
    @Column(name = "lote_proveedor")
    private String loteProveedor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detallesPedido;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum que coincide exactamente con la base de datos
    public enum EstadoLote {
        DISPONIBLE("Disponible"),
        VENDIDO("Vendido"),
        DEFECTUOSO("Defectuoso"),
        VENCIDO("Vencido");

        private final String displayName;

        EstadoLote(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Lote() {}

    public Lote(String codigoLote, Producto producto, LocalDate fechaProd, LocalDate fechaVenc, Integer cantidadTotal, Integer cantidadDisp) {
        this.codigoLote = codigoLote;
        this.producto = producto;
        this.fechaProd = fechaProd;
        this.fechaVenc = fechaVenc;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisp = cantidadDisp;
    }

    // Getters and Setters
    public Long getIdLote() { return idLote; }
    public void setIdLote(Long idLote) { this.idLote = idLote; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public LocalDate getFechaProd() { return fechaProd; }
    public void setFechaProd(LocalDate fechaProd) { this.fechaProd = fechaProd; }

    public LocalDate getFechaVenc() { return fechaVenc; }
    public void setFechaVenc(LocalDate fechaVenc) { this.fechaVenc = fechaVenc; }

    public Integer getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(Integer cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public Integer getCantidadDisp() { return cantidadDisp; }
    public void setCantidadDisp(Integer cantidadDisp) { this.cantidadDisp = cantidadDisp; }

    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getLoteProveedor() { return loteProveedor; }
    public void setLoteProveedor(String loteProveedor) { this.loteProveedor = loteProveedor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<DetallePedido> getDetallesPedido() { return detallesPedido; }
    public void setDetallesPedido(List<DetallePedido> detallesPedido) { this.detallesPedido = detallesPedido; }
}
