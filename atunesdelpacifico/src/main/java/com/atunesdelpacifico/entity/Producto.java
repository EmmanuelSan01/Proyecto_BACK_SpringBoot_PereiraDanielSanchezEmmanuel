package com.atunesdelpacifico.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El código SKU es obligatorio")
    @Size(max = 50, message = "El código SKU no puede exceder 50 caracteres")
    @Column(name = "codigo_sku", unique = true, nullable = false)
    private String codigoSku;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "conservante", nullable = false)
    private TipoConservante conservante;

    @Positive(message = "El contenido debe ser mayor a 0")
    @Column(name = "contenido_g", nullable = false)
    private Integer contenidoG;

    @DecimalMin(value = "0.01", message = "El precio de lista debe ser mayor a 0")
    @Column(name = "precio_lista", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioLista;

    @DecimalMin(value = "0.00", message = "El precio de costo no puede ser negativo")
    @Column(name = "precio_costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCosto = BigDecimal.ZERO;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // IMPORTANTE: Usar @JsonManagedReference para evitar ciclos infinitos
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Lote> lotes;

    public enum TipoConservante {
        ACEITE, AGUA, SALSA
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Producto() {}

    public Producto(String codigoSku, String nombre, String descripcion,
                    TipoConservante conservante, Integer contenidoG,
                    BigDecimal precioLista, BigDecimal precioCosto, Integer stockMinimo) {
        this.codigoSku = codigoSku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.conservante = conservante;
        this.contenidoG = contenidoG;
        this.precioLista = precioLista;
        this.precioCosto = precioCosto;
        this.stockMinimo = stockMinimo;
    }

    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoConservante getConservante() { return conservante; }
    public void setConservante(TipoConservante conservante) { this.conservante = conservante; }

    public Integer getContenidoG() { return contenidoG; }
    public void setContenidoG(Integer contenidoG) { this.contenidoG = contenidoG; }

    public BigDecimal getPrecioLista() { return precioLista; }
    public void setPrecioLista(BigDecimal precioLista) { this.precioLista = precioLista; }

    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Lote> getLotes() { return lotes; }
    public void setLotes(List<Lote> lotes) { this.lotes = lotes; }
}
