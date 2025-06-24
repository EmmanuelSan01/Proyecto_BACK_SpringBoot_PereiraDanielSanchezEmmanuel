package com.atunesdelpacifico.model.dto;

import java.math.BigDecimal;

public class DetallePedidoResponse {
    private Long idDetalle;
    private Long loteId;
    private String codigoLote;
    private String nombreProducto;
    private String descripcionProducto;
    private String conservanteProducto; // ✅ NUEVO CAMPO
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPct;
    private BigDecimal descuentoValor;
    private BigDecimal subtotal;

    // Constructores
    public DetallePedidoResponse() {}

    public DetallePedidoResponse(Long idDetalle, Long loteId, String codigoLote,
                                 String nombreProducto, String descripcionProducto,
                                 String conservanteProducto, // ✅ NUEVO PARÁMETRO
                                 Integer cantidad, BigDecimal precioUnitario,
                                 BigDecimal descuentoPct, BigDecimal descuentoValor,
                                 BigDecimal subtotal) {
        this.idDetalle = idDetalle;
        this.loteId = loteId;
        this.codigoLote = codigoLote;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.conservanteProducto = conservanteProducto; // ✅ ASIGNAR VALOR
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoPct = descuentoPct;
        this.descuentoValor = descuentoValor;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

    // ✅ NUEVO GETTER Y SETTER
    public String getConservanteProducto() { return conservanteProducto; }
    public void setConservanteProducto(String conservanteProducto) { this.conservanteProducto = conservanteProducto; }

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
}
