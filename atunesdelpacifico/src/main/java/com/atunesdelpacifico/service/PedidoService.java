package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.*;
import com.atunesdelpacifico.model.dto.DetallePedidoRequest;
import com.atunesdelpacifico.model.dto.PedidoRequest;
import com.atunesdelpacifico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private LoteService loteService;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> findByCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaPedidoDesc(clienteId);
    }

    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido crearPedido(PedidoRequest pedidoRequest) {
        Cliente cliente = clienteRepository.findById(pedidoRequest.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        for (DetallePedidoRequest detalle : pedidoRequest.getDetalles()) {
            if (!loteService.verificarDisponibilidad(detalle.getLoteId(), detalle.getCantidad())) {
                Lote lote = loteRepository.findById(detalle.getLoteId())
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
                throw new RuntimeException("Cantidad insuficiente en el lote: " + lote.getIdLote());
            }
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFechaEntrega(pedidoRequest.getFechaEntrega());
        pedido.setEstado(Pedido.EstadoPedido.Pendiente); // Corregido

        pedido = pedidoRepository.save(pedido);

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        for (DetallePedidoRequest detalleRequest : pedidoRequest.getDetalles()) {
            Lote lote = loteRepository.findById(detalleRequest.getLoteId())
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setLote(lote);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(lote.getProducto().getPrecioLista());
            detalle.setDescuentoPct(detalleRequest.getDescuentoPorcentaje()); // Corregido

            detalle = detallePedidoRepository.save(detalle);
            detalles.add(detalle);

            totalPedido = totalPedido.add(detalle.getSubtotal());

            loteService.reducirCantidadDisponible(lote.getIdLote(), detalle.getCantidad());
        }

        pedido.setTotal(totalPedido);
        pedido.setDetalles(detalles);

        return pedidoRepository.save(pedido);
    }

    public Pedido cambiarEstado(Long id, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() == Pedido.EstadoPedido.Enviado) { // Corregido
            throw new RuntimeException("No se puede cancelar un pedido ya enviado");
        }

        List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
        for (DetallePedido detalle : detalles) {
            Lote lote = detalle.getLote();
            lote.setCantidadDisp(lote.getCantidadDisp() + detalle.getCantidad()); // Corregido

            if (lote.getEstado() == Lote.EstadoLote.VENDIDO && lote.getCantidadDisp() > 0) { // Corregido
                lote.setEstado(Lote.EstadoLote.DISPONIBLE); // Corregido
            }

            loteRepository.save(lote);
        }

        pedido.setEstado(Pedido.EstadoPedido.Cancelado); // Corregido
        pedidoRepository.save(pedido);
    }

    public List<Pedido> findByFechaPedido(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public Long countByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }
}
