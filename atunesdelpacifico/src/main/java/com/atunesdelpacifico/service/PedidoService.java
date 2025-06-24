package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.*;
import com.atunesdelpacifico.model.dto.DetallePedidoRequest;
import com.atunesdelpacifico.model.dto.PedidoRequest;
import com.atunesdelpacifico.model.dto.PedidoResponse;
import com.atunesdelpacifico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.atunesdelpacifico.model.dto.DetallePedidoResponse;

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

    public List<PedidoResponse> findAllAsResponse() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToResponseWithDetails)
                .collect(Collectors.toList());
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<PedidoResponse> findByIdAsResponse(Long id) {
        return pedidoRepository.findById(id)
                .map(this::convertToResponseWithDetails);
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

    public List<PedidoResponse> findByUsuarioNombreAsResponse(String nombreUsuario) {
        System.out.println("=== SERVICIO: Obteniendo pedidos para usuario: " + nombreUsuario + " ===");

        List<Pedido> pedidos = findByUsuarioNombre(nombreUsuario);
        System.out.println("Pedidos encontrados en BD: " + pedidos.size());

        List<PedidoResponse> response = pedidos.stream()
                .map(this::convertToResponseWithDetails)
                .collect(Collectors.toList());

        System.out.println("Pedidos convertidos a response: " + response.size());

        return response;
    }

    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findByFechaPedido(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public Pedido crearPedido(PedidoRequest pedidoRequest, String nombreUsuario) {
        try {
            System.out.println("=== CREANDO PEDIDO ===");
            System.out.println("Usuario: " + nombreUsuario);
            System.out.println("Detalles recibidos: " + pedidoRequest.getDetalles().size());

            // Debug de cada detalle recibido
            for (int i = 0; i < pedidoRequest.getDetalles().size(); i++) {
                DetallePedidoRequest detalle = pedidoRequest.getDetalles().get(i);
                System.out.println("Detalle " + i + " - Lote ID: " + detalle.getLoteId());
                System.out.println("Detalle " + i + " - Cantidad: " + detalle.getCantidad());

                // Verificar el lote y producto asociado
                Optional<Lote> loteOpt = loteRepository.findById(detalle.getLoteId());
                if (loteOpt.isPresent()) {
                    Lote lote = loteOpt.get();
                    System.out.println("Detalle " + i + " - Lote encontrado: " + lote.getCodigoLote());
                    System.out.println("Detalle " + i + " - Producto: " + lote.getProducto().getNombre());
                    System.out.println("Detalle " + i + " - Conservante: " + lote.getProducto().getConservante());
                } else {
                    System.out.println("Detalle " + i + " - LOTE NO ENCONTRADO!");
                }
            }

            // Obtener el cliente
            Cliente cliente;
            if (pedidoRequest.getClienteId() != null) {
                cliente = clienteRepository.findById(pedidoRequest.getClienteId())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            } else {
                cliente = clienteRepository.findByUsuarioNombreUsuario(nombreUsuario)
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el usuario: " + nombreUsuario));
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

            Usuario createdBy = usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
            pedido.setCreatedBy(createdBy);

            pedido = pedidoRepository.save(pedido);
            System.out.println("Pedido guardado con ID: " + pedido.getIdPedido());

            // Crear los detalles del pedido
            BigDecimal subtotalPedido = BigDecimal.ZERO;
            List<DetallePedido> detallesList = new ArrayList<>();

            for (DetallePedidoRequest detalleRequest : pedidoRequest.getDetalles()) {
                Lote lote = loteRepository.findById(detalleRequest.getLoteId())
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + detalleRequest.getLoteId()));

                System.out.println("Procesando lote: " + lote.getCodigoLote() + " - Producto: " + lote.getProducto().getNombre());

                if (lote.getCantidadDisp() < detalleRequest.getCantidad()) {
                    throw new RuntimeException("Cantidad insuficiente en el lote: " + lote.getCodigoLote());
                }

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setLote(lote);
                detalle.setCantidad(detalleRequest.getCantidad());
                detalle.setPrecioUnitario(detalleRequest.getPrecioUnitario());
                detalle.setDescuentoPct(detalleRequest.getDescuentoPct());
                detalle.setDescuentoValor(detalleRequest.getDescuentoValor());

                BigDecimal subtotalDetalle = detalleRequest.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));
                detalle.setSubtotal(subtotalDetalle);

                detalle = detallePedidoRepository.save(detalle);
                detallesList.add(detalle);
                subtotalPedido = subtotalPedido.add(subtotalDetalle);

                System.out.println("Detalle guardado - ID: " + detalle.getIdDetalle() + " - Producto: " + lote.getProducto().getNombre());
            }

            // Calcular descuentos por cantidad
            int totalProductos = pedidoRequest.getDetalles().stream()
                    .mapToInt(DetallePedidoRequest::getCantidad)
                    .sum();
            int gruposDescuento = totalProductos / 10;
            BigDecimal porcentajeDescuento = BigDecimal.valueOf(gruposDescuento * 1.0);
            BigDecimal descuentoTotal = subtotalPedido
                    .multiply(porcentajeDescuento)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Calcular envío
            BigDecimal subtotalConDescuento = subtotalPedido.subtract(descuentoTotal);
            BigDecimal costoEnvio = subtotalConDescuento.compareTo(BigDecimal.valueOf(50)) >= 0 ?
                    BigDecimal.ZERO : BigDecimal.valueOf(5.00);

            // Actualizar totales del pedido
            pedido.setSubtotal(subtotalPedido);
            pedido.setDescuento(descuentoTotal);
            pedido.setTotal(subtotalConDescuento.add(costoEnvio));
            pedido.setDetalles(detallesList);

            Pedido pedidoFinal = pedidoRepository.save(pedido);
            System.out.println("Pedido final guardado - Total: " + pedidoFinal.getTotal());

            return pedidoFinal;

        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear pedido: " + e.getMessage(), e);
        }
    }

    public Pedido cambiarEstado(Long id, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

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

        List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
        for (DetallePedido detalle : detalles) {
            Lote lote = detalle.getLote();
            int nuevaCantidad = lote.getCantidadDisp() - detalle.getCantidad();
            lote.setCantidadDisp(nuevaCantidad);

            if (nuevaCantidad <= 0) {
                lote.setEstado(Lote.EstadoLote.VENDIDO);
            }

            loteRepository.save(lote);
        }

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

    public PedidoResponse convertToResponse(Pedido pedido) {
        // Obtener los detalles del pedido
        List<DetallePedido> detallesPedido = detallePedidoRepository.findByPedido(pedido);

        // Convertir detalles a DTO
        List<DetallePedidoResponse> detallesResponse = detallesPedido.stream()
                .map(this::convertDetalleToResponse)
                .collect(Collectors.toList());

        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getNumeroPedido(),
                pedido.getCliente().getNombre(),
                pedido.getFechaPedido(),
                pedido.getFechaEntrega(),
                pedido.getEstado().toString(),
                pedido.getSubtotal(),
                pedido.getDescuento(),
                pedido.getTotal(),
                pedido.getMetodoPago().toString(),
                detallesResponse
        );
    }

    private DetallePedidoResponse convertDetalleToResponse(DetallePedido detalle) {
        System.out.println("=== CONVIRTIENDO DETALLE ===");
        System.out.println("Detalle ID: " + detalle.getIdDetalle());
        System.out.println("Lote ID: " + detalle.getLote().getIdLote());
        System.out.println("Código Lote: " + detalle.getLote().getCodigoLote());
        System.out.println("Producto: " + detalle.getLote().getProducto().getNombre());
        System.out.println("Conservante: " + detalle.getLote().getProducto().getConservante());

        return new DetallePedidoResponse(
                detalle.getIdDetalle(),
                detalle.getLote().getIdLote(),
                detalle.getLote().getCodigoLote(),
                detalle.getLote().getProducto().getNombre(),
                detalle.getLote().getProducto().getDescripcion(),
                detalle.getLote().getProducto().getConservante().toString(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getDescuentoPct(),
                detalle.getDescuentoValor(),
                detalle.getSubtotal()
        );
    }

    public PedidoResponse convertToResponseWithDetails(Pedido pedido) {
        System.out.println("=== CONVIRTIENDO PEDIDO CON DETALLES ===");
        System.out.println("Pedido ID: " + pedido.getIdPedido());

        // ✅ USAR QUERY DIRECTO PARA DEBUG
        List<Object[]> detallesRaw = detallePedidoRepository.findDetallesPedidoCompleto(pedido.getIdPedido());
        System.out.println("Detalles RAW encontrados: " + detallesRaw.size());

        List<DetallePedidoResponse> detallesResponse = new ArrayList<>();

        for (Object[] row : detallesRaw) {
            Long idDetalle = (Long) row[0];
            Integer cantidad = (Integer) row[1];
            BigDecimal precioUnitario = (BigDecimal) row[2];
            BigDecimal subtotal = (BigDecimal) row[3];
            Long loteId = (Long) row[4];
            String codigoLote = (String) row[5];
            Long productoId = (Long) row[6];
            String nombreProducto = (String) row[7];
            String descripcionProducto = (String) row[8];
            String conservante = row[9].toString();

            System.out.println("=== DETALLE RAW ===");
            System.out.println("ID Detalle: " + idDetalle);
            System.out.println("Lote ID: " + loteId);
            System.out.println("Código Lote: " + codigoLote);
            System.out.println("Producto: " + nombreProducto);
            System.out.println("Conservante: " + conservante);

            DetallePedidoResponse detalle = new DetallePedidoResponse(
                    idDetalle,
                    loteId,
                    codigoLote,
                    nombreProducto,
                    descripcionProducto,
                    conservante,
                    cantidad,
                    precioUnitario,
                    BigDecimal.ZERO, // descuentoPct
                    BigDecimal.ZERO, // descuentoValor
                    subtotal
            );

            detallesResponse.add(detalle);
        }

        PedidoResponse response = new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getNumeroPedido(),
                pedido.getCliente().getNombre(),
                pedido.getFechaPedido(),
                pedido.getFechaEntrega(),
                pedido.getEstado().toString(),
                pedido.getSubtotal(),
                pedido.getDescuento(),
                pedido.getTotal(),
                pedido.getMetodoPago().toString(),
                detallesResponse
        );

        System.out.println("Response creado con " + response.getDetalles().size() + " detalles");
        return response;
    }

    private String generarNumeroPedido() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "PED-" + timestamp;
    }
}
