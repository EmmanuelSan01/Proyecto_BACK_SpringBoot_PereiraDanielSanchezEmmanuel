package com.atunesdelpacifico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "conservante", nullable = false)
    private TipoConservante conservante;
    
    @Min(value = 1, message = "El contenido debe ser mayor a 0")
    @Column(name = "contenido_g", nullable = false)
    private Integer contenidoGramos;
    
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio_lista", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioLista;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lote> lotes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Enum
    public enum TipoConservante {
        ACEITE("aceite"),
        AGUA("agua"),
        SALSA("salsa");
        
        private final String displayName;
        
        TipoConservante(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Producto() {}
    
    public Producto(String nombre, TipoConservante conservante, Integer contenidoGramos, BigDecimal precioLista) {
        this.nombre = nombre;
        this.conservante = conservante;
        this.contenidoGramos = contenidoGramos;
        this.precioLista = precioLista;
    }
    
    // Getters and Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public TipoConservante getConservante() { return conservante; }
    public void setConservante(TipoConservante conservante) { this.conservante = conservante; }
    
    public Integer getContenidoGramos() { return contenidoGramos; }
    public void setContenidoGramos(Integer contenidoGramos) { this.contenidoGramos = contenidoGramos; }
    
    public BigDecimal getPrecioLista() { return precioLista; }
    public void setPrecioLista(BigDecimal precioLista) { this.precioLista = precioLista; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<Lote> getLotes() { return lotes; }
    public void setLotes(List<Lote> lotes) { this.lotes = lotes; }
}
