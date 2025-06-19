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
import java.time.format.DateTimeFormatter;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LoteService loteService;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaPedidoDesc(clienteId);
    }

    public List<Pedido> findByUsuarioNombre(String nombreUsuario) {
        Optional<Cliente> cliente = clienteRepository.findByUsuarioNombreUsuario(nombreUsuario);
        if (cliente.isPresent()) {
            return pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente.get());
        }
        return new ArrayList<>();
    }

    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findByFechaPedido(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public Pedido crearPedido(PedidoRequest pedidoRequest, String nombreUsuario) {
        // Obtener el cliente
        Cliente cliente;
        if (pedidoRequest.getClienteId() != null) {
            // Admin/Operador creando pedido para un cliente específico
            cliente = clienteRepository.findById(pedidoRequest.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        } else {
            // Cliente creando su propio pedido
            cliente = clienteRepository.findByUsuarioNombreUsuario(nombreUsuario)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        }

        // Generar número de pedido único
        String numeroPedido = generarNumeroPedido();

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(numeroPedido);
        pedido.setCliente(cliente);
        pedido.setFechaEntrega(pedidoRequest.getFechaEntrega());
        pedido.setMetodoPago(pedidoRequest.getMetodoPago());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);

        // Obtener usuario que crea el pedido
        Usuario createdBy = usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
        pedido.setCreatedBy(createdBy);

        pedido = pedidoRepository.save(pedido);

        // Crear los detalles del pedido
        BigDecimal subtotalPedido = BigDecimal.ZERO;
        for (DetallePedidoRequest detalleRequest : pedidoRequest.getDetalles()) {
            Lote lote = loteRepository.findById(detalleRequest.getLoteId())
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + detalleRequest.getLoteId()));

            // Verificar disponibilidad
            if (!loteService.verificarDisponibilidad(lote.getIdLote(), detalleRequest.getCantidad())) {
                throw new RuntimeException("Cantidad insuficiente en el lote: " + lote.getCodigoLote());
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setLote(lote);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(detalleRequest.getPrecioUnitario());
            detalle.setDescuentoPct(detalleRequest.getDescuentoPct());
            detalle.setDescuentoValor(detalleRequest.getDescuentoValor());

            detallePedidoRepository.save(detalle);
            subtotalPedido = subtotalPedido.add(detalle.getSubtotal());
        }

        // Actualizar totales del pedido
        pedido.setSubtotal(subtotalPedido);
        pedido.setTotal(subtotalPedido.subtract(pedido.getDescuento()).add(pedido.getImpuestos()));

        return pedidoRepository.save(pedido);
    }

    public Pedido update(Long id, PedidoRequest pedidoRequest) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Solo se puede actualizar si está en estado PENDIENTE
        if (pedido.getEstado() != Pedido.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden actualizar pedidos en estado PENDIENTE");
        }

        pedido.setFechaEntrega(pedidoRequest.getFechaEntrega());
        pedido.setMetodoPago(pedidoRequest.getMetodoPago());

        return pedidoRepository.save(pedido);
    }

    public void deleteById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Solo se puede eliminar si está en estado PENDIENTE
        if (pedido.getEstado() != Pedido.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden eliminar pedidos en estado PENDIENTE");
        }

        pedidoRepository.deleteById(id);
    }

    public Pedido cambiarEstado(Long id, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

        // Si se marca como entregado, actualizar fecha de entrega real
        if (nuevoEstado == Pedido.EstadoPedido.ENTREGADO) {
            pedido.setFechaEntregaReal(java.time.LocalDate.now());
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido confirmarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() != Pedido.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar pedidos en estado PENDIENTE");
        }

        // Reducir inventario de cada lote
        List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
        for (DetallePedido detalle : detalles) {
            loteService.reducirCantidadDisponible(detalle.getLote().getIdLote(), detalle.getCantidad());
        }

        // Cambiar estado a EN_PROCESO
        pedido.setEstado(Pedido.EstadoPedido.EN_PROCESO);

        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() == Pedido.EstadoPedido.CANCELADO) {
            throw new RuntimeException("El pedido ya está cancelado");
        }

        if (pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO) {
            throw new RuntimeException("No se puede cancelar un pedido ya entregado");
        }

        // Si el pedido estaba confirmado, restaurar inventario
        if (pedido.getEstado() == Pedido.EstadoPedido.EN_PROCESO || pedido.getEstado() == Pedido.EstadoPedido.ENVIADO) {
            List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
            for (DetallePedido detalle : detalles) {
                Lote lote = detalle.getLote();
                lote.setCantidadDisp(lote.getCantidadDisp() + detalle.getCantidad());
                if (lote.getEstado() == Lote.EstadoLote.VENDIDO && lote.getCantidadDisp() > 0) {
                    lote.setEstado(Lote.EstadoLote.DISPONIBLE);
                }
                loteRepository.save(lote);
            }
        }

        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    public boolean isOwner(Long pedidoId, String nombreUsuario) {
        Optional<Pedido> pedido = pedidoRepository.findById(pedidoId);
        if (pedido.isPresent()) {
            return pedido.get().getCliente().getUsuario().getNombreUsuario().equals(nombreUsuario);
        }
        return false;
    }

    public boolean isClienteOwner(Long clienteId, String nombreUsuario) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        return cliente.isPresent() &&
                cliente.get().getUsuario().getNombreUsuario().equals(nombreUsuario);
    }

    private String generarNumeroPedido() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "PED-" + timestamp;
    }
}
