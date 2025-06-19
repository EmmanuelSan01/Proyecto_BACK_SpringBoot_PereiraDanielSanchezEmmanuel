package com.atunesdelpacifico.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DetallePedidoRequest {

    @NotNull(message = "El ID del lote es obligatorio")
    private Long loteId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.00", message = "El descuento porcentual no puede ser negativo")
    private BigDecimal descuentoPct = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El descuento en valor no puede ser negativo")
    private BigDecimal descuentoValor = BigDecimal.ZERO;

    // Constructors
    public DetallePedidoRequest() {}

    public DetallePedidoRequest(Long loteId, Integer cantidad, BigDecimal precioUnitario) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters and Setters
    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getDescuentoPct() { return descuentoPct; }
    public void setDescuentoPct(BigDecimal descuentoPct) { this.descuentoPct = descuentoPct; }

    public BigDecimal getDescuentoValor() { return descuentoValor; }
    public void setDescuentoValor(BigDecimal descuentoValor) { this.descuentoValor = descuentoValor; }
}
