package com.atunesdelpacifico.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetallePedidoRequest {
    
    @NotNull(message = "El ID del lote es obligatorio")
    private Long loteId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    
    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede ser mayor a 100%")
    private Byte descuentoPorcentaje = 0;
    
    // Constructors
    public DetallePedidoRequest() {}
    
    public DetallePedidoRequest(Long loteId, Integer cantidad, Byte descuentoPorcentaje) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.descuentoPorcentaje = descuentoPorcentaje;
    }
    
    // Getters and Setters
    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public Byte getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(Byte descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }
}
