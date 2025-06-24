package com.atunesdelpacifico.model.dto;

import com.atunesdelpacifico.entity.Pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class PedidoRequest {

    private Long clienteId; // Opcional, si no se proporciona se usa el cliente autenticado

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDate fechaEntrega;

    private Pedido.MetodoPago metodoPago = Pedido.MetodoPago.TRANSFERENCIA;

    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid
    private List<DetallePedidoRequest> detalles;

    // Constructors
    public PedidoRequest() {}

    // Getters and Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public Pedido.MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(Pedido.MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public List<DetallePedidoRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoRequest> detalles) { this.detalles = detalles; }
}
