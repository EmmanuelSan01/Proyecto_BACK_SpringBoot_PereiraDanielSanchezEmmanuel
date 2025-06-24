package com.atunesdelpacifico.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {
    private Long idPedido;
    private String numeroPedido;
    private String clienteNombre;
    private LocalDateTime fechaPedido;
    private LocalDate fechaEntrega;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private String metodoPago;
    private List<DetallePedidoResponse> detalles;

    // Constructores
    public PedidoResponse() {}

    public PedidoResponse(Long idPedido, String numeroPedido, String clienteNombre,
                          LocalDateTime fechaPedido, LocalDate fechaEntrega, String estado,
                          BigDecimal subtotal, BigDecimal descuento, BigDecimal total, String metodoPago) {
        this.idPedido = idPedido;
        this.numeroPedido = numeroPedido;
        this.clienteNombre = clienteNombre;
        this.fechaPedido = fechaPedido;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.metodoPago = metodoPago;
    }

    public PedidoResponse(Long idPedido, String numeroPedido, String clienteNombre,
                          LocalDateTime fechaPedido, LocalDate fechaEntrega, String estado,
                          BigDecimal subtotal, BigDecimal descuento, BigDecimal total,
                          String metodoPago, List<DetallePedidoResponse> detalles) {
        this.idPedido = idPedido;
        this.numeroPedido = numeroPedido;
        this.clienteNombre = clienteNombre;
        this.fechaPedido = fechaPedido;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.metodoPago = metodoPago;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public List<DetallePedidoResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoResponse> detalles) { this.detalles = detalles; }
}
