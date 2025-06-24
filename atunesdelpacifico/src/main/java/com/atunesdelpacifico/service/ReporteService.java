package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.model.dto.DashboardStatsDTO;
import com.atunesdelpacifico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> getReporteVentasPorProducto(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        try {
            LocalDateTime fechaInicioDateTime = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
            LocalDateTime fechaFinDateTime = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

            List<Pedido> pedidos;
            if (fechaInicioDateTime != null && fechaFinDateTime != null) {
                pedidos = pedidoRepository.findByFechaPedidoBetween(fechaInicioDateTime, fechaFinDateTime);
            } else {
                pedidos = pedidoRepository.findAll();
            }

            // Agrupar ventas por tipo de conservante
            Map<String, Object> ventasPorTipo = new HashMap<>();
            Map<String, Integer> cantidadPorTipo = new HashMap<>();
            Map<String, Double> totalPorTipo = new HashMap<>();

            for (Pedido pedido : pedidos) {
                if (pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO ||
                        pedido.getEstado() == Pedido.EstadoPedido.ENVIADO) {

                    if (pedido.getDetalles() != null) {
                        pedido.getDetalles().forEach(detalle -> {
                            if (detalle.getLote() != null && detalle.getLote().getProducto() != null) {
                                String tipoConservante = detalle.getLote().getProducto().getConservante().toString();
                                cantidadPorTipo.merge(tipoConservante, detalle.getCantidad(), Integer::sum);
                                totalPorTipo.merge(tipoConservante, detalle.getSubtotal().doubleValue(), Double::sum);
                            }
                        });
                    }
                }
            }

            ventasPorTipo.put("cantidadPorTipo", cantidadPorTipo);
            ventasPorTipo.put("totalPorTipo", totalPorTipo);

            reporte.put("titulo", "Reporte de Ventas por Producto");
            reporte.put("fechaInicio", fechaInicio);
            reporte.put("fechaFin", fechaFin);
            reporte.put("datos", ventasPorTipo);
            reporte.put("totalPedidos", pedidos.size());

        } catch (Exception e) {
            reporte.put("error", "Error al generar reporte: " + e.getMessage());
            reporte.put("datos", new HashMap<>());
        }

        return reporte;
    }

    public Map<String, Object> getReporteVentasPorCliente(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        try {
            LocalDateTime fechaInicioDateTime = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
            LocalDateTime fechaFinDateTime = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

            List<Pedido> pedidos;
            if (fechaInicioDateTime != null && fechaFinDateTime != null) {
                pedidos = pedidoRepository.findByFechaPedidoBetween(fechaInicioDateTime, fechaFinDateTime);
            } else {
                pedidos = pedidoRepository.findAll();
            }

            // Agrupar ventas por cliente
            Map<String, Object> ventasPorCliente = pedidos.stream()
                    .filter(p -> p.getEstado() == Pedido.EstadoPedido.ENTREGADO ||
                            p.getEstado() == Pedido.EstadoPedido.ENVIADO)
                    .filter(p -> p.getCliente() != null)
                    .collect(Collectors.groupingBy(
                            p -> p.getCliente().getNombre() != null ? p.getCliente().getNombre() : "Cliente sin nombre",
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    pedidosCliente -> {
                                        Map<String, Object> clienteData = new HashMap<>();
                                        clienteData.put("totalPedidos", pedidosCliente.size());
                                        clienteData.put("totalVentas", pedidosCliente.stream()
                                                .mapToDouble(p -> p.getTotal() != null ? p.getTotal().doubleValue() : 0.0).sum());
                                        return clienteData;
                                    }
                            )
                    ));

            reporte.put("titulo", "Reporte de Ventas por Cliente");
            reporte.put("fechaInicio", fechaInicio);
            reporte.put("fechaFin", fechaFin);
            reporte.put("datos", ventasPorCliente);

        } catch (Exception e) {
            reporte.put("error", "Error al generar reporte: " + e.getMessage());
            reporte.put("datos", new HashMap<>());
        }

        return reporte;
    }

    public Map<String, Object> getReporteProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        try {
            List<Lote> lotes;
            if (fechaInicio != null && fechaFin != null) {
                lotes = loteRepository.findByFechaProdBetween(fechaInicio, fechaFin);
            } else {
                lotes = loteRepository.findAll();
            }

            // Estadísticas de producción
            Map<String, Object> estadisticasProduccion = new HashMap<>();
            estadisticasProduccion.put("totalLotes", lotes.size());
            estadisticasProduccion.put("totalProduccion", lotes.stream()
                    .mapToInt(Lote::getCantidadTotal).sum());

            // Producción por tipo de conservante
            Map<String, Integer> produccionPorTipo = lotes.stream()
                    .filter(l -> l.getProducto() != null && l.getProducto().getConservante() != null)
                    .collect(Collectors.groupingBy(
                            l -> l.getProducto().getConservante().toString(),
                            Collectors.summingInt(Lote::getCantidadTotal)
                    ));

            estadisticasProduccion.put("produccionPorTipo", produccionPorTipo);

            reporte.put("titulo", "Reporte de Producción");
            reporte.put("fechaInicio", fechaInicio);
            reporte.put("fechaFin", fechaFin);
            reporte.put("datos", estadisticasProduccion);

        } catch (Exception e) {
            reporte.put("error", "Error al generar reporte: " + e.getMessage());
            reporte.put("datos", new HashMap<>());
        }

        return reporte;
    }

    public Map<String, Object> getReporteInventario() {
        Map<String, Object> reporte = new HashMap<>();

        try {
            Long totalLotes = loteRepository.count();
            Long lotesDisponibles = loteRepository.countByEstado(Lote.EstadoLote.DISPONIBLE);
            Long lotesVendidos = loteRepository.countByEstado(Lote.EstadoLote.VENDIDO);
            Long lotesDefectuosos = loteRepository.countByEstado(Lote.EstadoLote.DEFECTUOSO);

            // Inventario disponible por producto
            List<Lote> lotesDisponiblesList = loteRepository.findByEstado(Lote.EstadoLote.DISPONIBLE);
            Map<String, Integer> inventarioPorProducto = lotesDisponiblesList.stream()
                    .filter(l -> l.getProducto() != null && l.getProducto().getNombre() != null)
                    .collect(Collectors.groupingBy(
                            l -> l.getProducto().getNombre(),
                            Collectors.summingInt(Lote::getCantidadDisp)
                    ));

            reporte.put("titulo", "Reporte de Inventario");
            reporte.put("totalLotes", totalLotes);
            reporte.put("lotesDisponibles", lotesDisponibles);
            reporte.put("lotesVendidos", lotesVendidos);
            reporte.put("lotesDefectuosos", lotesDefectuosos);
            reporte.put("inventarioPorProducto", inventarioPorProducto);

        } catch (Exception e) {
            reporte.put("error", "Error al generar reporte: " + e.getMessage());
            reporte.put("totalLotes", 0L);
            reporte.put("lotesDisponibles", 0L);
            reporte.put("lotesVendidos", 0L);
            reporte.put("lotesDefectuosos", 0L);
            reporte.put("inventarioPorProducto", new HashMap<>());
        }

        return reporte;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        try {
            System.out.println("=== Iniciando getDashboardStats ===");

            // Estadísticas generales con manejo de errores individual
            try {
                stats.setTotalUsuarios(usuarioRepository.count());
            } catch (Exception e) {
                System.err.println("Error contando usuarios: " + e.getMessage());
                stats.setTotalUsuarios(0L);
            }

            try {
                stats.setTotalClientes(clienteRepository.count());
            } catch (Exception e) {
                System.err.println("Error contando clientes: " + e.getMessage());
                stats.setTotalClientes(0L);
            }

            try {
                stats.setTotalProductos(productoRepository.count());
            } catch (Exception e) {
                System.err.println("Error contando productos: " + e.getMessage());
                stats.setTotalProductos(0L);
            }

            try {
                stats.setTotalPedidos(pedidoRepository.count());
            } catch (Exception e) {
                System.err.println("Error contando pedidos: " + e.getMessage());
                stats.setTotalPedidos(0L);
            }

            // Estadísticas de pedidos por estado
            try {
                stats.setPedidosPendientes(pedidoRepository.countByEstado(Pedido.EstadoPedido.PENDIENTE));
                stats.setPedidosEnProceso(pedidoRepository.countByEstado(Pedido.EstadoPedido.EN_PROCESO));
                stats.setPedidosEnviados(pedidoRepository.countByEstado(Pedido.EstadoPedido.ENVIADO));
                stats.setPedidosEntregados(pedidoRepository.countByEstado(Pedido.EstadoPedido.ENTREGADO));
                stats.setPedidosCancelados(pedidoRepository.countByEstado(Pedido.EstadoPedido.CANCELADO));
            } catch (Exception e) {
                System.err.println("Error contando pedidos por estado: " + e.getMessage());
                stats.setPedidosPendientes(0L);
                stats.setPedidosEnProceso(0L);
                stats.setPedidosEnviados(0L);
                stats.setPedidosEntregados(0L);
                stats.setPedidosCancelados(0L);
            }

            // Inventario
            try {
                stats.setTotalLotes(loteRepository.count());
                stats.setLotesDisponibles(loteRepository.countByEstado(Lote.EstadoLote.DISPONIBLE));
                stats.setLotesDefectuosos(loteRepository.countByEstado(Lote.EstadoLote.DEFECTUOSO));
            } catch (Exception e) {
                System.err.println("Error contando lotes: " + e.getMessage());
                stats.setTotalLotes(0L);
                stats.setLotesDisponibles(0L);
                stats.setLotesDefectuosos(0L);
            }

            // Total de ventas
            try {
                Double totalVentas = pedidoRepository.getTotalVentas();
                stats.setTotalVentas(totalVentas != null ? totalVentas : 0.0);
            } catch (Exception e) {
                System.err.println("Error calculando total ventas: " + e.getMessage());
                stats.setTotalVentas(0.0);
            }

            // Productos más vendidos
            try {
                List<Object[]> productosPopularesRaw = detallePedidoRepository.findProductosMasVendidos(PageRequest.of(0, 5));
                List<DashboardStatsDTO.ProductoPopularDTO> productosPopulares = productosPopularesRaw.stream()
                        .map(row -> new DashboardStatsDTO.ProductoPopularDTO(
                                (String) row[0],
                                ((Number) row[1]).longValue()
                        ))
                        .collect(Collectors.toList());
                stats.setProductosPopulares(productosPopulares);
            } catch (Exception e) {
                System.err.println("Error obteniendo productos populares: " + e.getMessage());
                stats.setProductosPopulares(new ArrayList<>());
            }

            // Pedidos recientes
            try {
                List<Pedido> pedidosRecientesRaw = pedidoRepository.findTop10ByOrderByFechaPedidoDesc(PageRequest.of(0, 10));
                List<DashboardStatsDTO.PedidoRecenteDTO> pedidosRecientes = pedidosRecientesRaw.stream()
                        .map(pedido -> {
                            String clienteNombre = "Cliente desconocido";
                            try {
                                if (pedido.getCliente() != null && pedido.getCliente().getNombre() != null) {
                                    clienteNombre = pedido.getCliente().getNombre();
                                }
                            } catch (Exception e) {
                                // Ignorar errores de lazy loading
                            }

                            return new DashboardStatsDTO.PedidoRecenteDTO(
                                    pedido.getIdPedido(),
                                    pedido.getNumeroPedido(),
                                    clienteNombre,
                                    pedido.getEstado().toString(),
                                    pedido.getTotal() != null ? pedido.getTotal().doubleValue() : 0.0,
                                    pedido.getFechaPedido().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            );
                        })
                        .collect(Collectors.toList());
                stats.setPedidosRecientes(pedidosRecientes);
            } catch (Exception e) {
                System.err.println("Error obteniendo pedidos recientes: " + e.getMessage());
                stats.setPedidosRecientes(new ArrayList<>());
            }

            System.out.println("Dashboard stats generadas exitosamente");

        } catch (Exception e) {
            System.err.println("Error general en getDashboardStats: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar estadísticas del dashboard", e);
        }

        return stats;
    }

    public Map<String, Object> getNotificaciones() {
        Map<String, Object> notificaciones = new HashMap<>();

        try {
            // Pedidos que requieren atención
            Long pedidosPendientes = pedidoRepository.countByEstado(Pedido.EstadoPedido.PENDIENTE);

            // Lotes defectuosos
            Long lotesDefectuosos = loteRepository.countByEstado(Lote.EstadoLote.DEFECTUOSO);

            // Lotes próximos a vencer (30 días)
            LocalDate fechaLimite = LocalDate.now().plusDays(30);
            Long lotesProximosVencer = loteRepository.countByFechaVencBefore(fechaLimite);

            // Productos con stock bajo
            Long productosStockBajo = 0L;
            try {
                List<Lote> lotesDisponibles = loteRepository.findByEstado(Lote.EstadoLote.DISPONIBLE);
                Map<Long, Integer> stockPorProducto = lotesDisponibles.stream()
                        .filter(l -> l.getProducto() != null)
                        .collect(Collectors.groupingBy(
                                l -> l.getProducto().getIdProducto(),
                                Collectors.summingInt(Lote::getCantidadDisp)
                        ));

                productosStockBajo = stockPorProducto.entrySet().stream()
                        .mapToLong(entry -> {
                            try {
                                Producto producto = productoRepository.findById(entry.getKey()).orElse(null);
                                return (producto != null && entry.getValue() < producto.getStockMinimo()) ? 1 : 0;
                            } catch (Exception e) {
                                return 0;
                            }
                        }).sum();
            } catch (Exception e) {
                productosStockBajo = 0L;
            }

            notificaciones.put("pedidosPendientes", pedidosPendientes);
            notificaciones.put("lotesDefectuosos", lotesDefectuosos);
            notificaciones.put("lotesProximosVencer", lotesProximosVencer);
            notificaciones.put("productosStockBajo", productosStockBajo);
            notificaciones.put("totalNotificaciones", pedidosPendientes + lotesDefectuosos + lotesProximosVencer + productosStockBajo);

        } catch (Exception e) {
            notificaciones.put("pedidosPendientes", 0L);
            notificaciones.put("lotesDefectuosos", 0L);
            notificaciones.put("lotesProximosVencer", 0L);
            notificaciones.put("productosStockBajo", 0L);
            notificaciones.put("totalNotificaciones", 0L);
            notificaciones.put("error", "Error al cargar notificaciones: " + e.getMessage());
        }

        return notificaciones;
    }
}
