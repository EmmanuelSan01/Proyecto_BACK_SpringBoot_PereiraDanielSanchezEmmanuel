package com.atunesdelpacifico.model.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    private Long totalUsuarios;
    private Long totalClientes;
    private Long totalProductos;
    private Long totalPedidos;
    private Long pedidosPendientes;
    private Long pedidosEnProceso;
    private Long pedidosEnviados;
    private Long pedidosEntregados;
    private Long pedidosCancelados;
    private Long totalLotes;
    private Long lotesDisponibles;
    private Long lotesDefectuosos;
    private Double totalVentas;
    private List<ProductoPopularDTO> productosPopulares;
    private List<PedidoRecenteDTO> pedidosRecientes;

    // Constructors
    public DashboardStatsDTO() {}

    // Getters and Setters
    public Long getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(Long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public Long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public Long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public Long getPedidosPendientes() {
        return pedidosPendientes;
    }

    public void setPedidosPendientes(Long pedidosPendientes) {
        this.pedidosPendientes = pedidosPendientes;
    }

    public Long getPedidosEnProceso() {
        return pedidosEnProceso;
    }

    public void setPedidosEnProceso(Long pedidosEnProceso) {
        this.pedidosEnProceso = pedidosEnProceso;
    }

    public Long getPedidosEnviados() {
        return pedidosEnviados;
    }

    public void setPedidosEnviados(Long pedidosEnviados) {
        this.pedidosEnviados = pedidosEnviados;
    }

    public Long getPedidosEntregados() {
        return pedidosEntregados;
    }

    public void setPedidosEntregados(Long pedidosEntregados) {
        this.pedidosEntregados = pedidosEntregados;
    }

    public Long getPedidosCancelados() {
        return pedidosCancelados;
    }

    public void setPedidosCancelados(Long pedidosCancelados) {
        this.pedidosCancelados = pedidosCancelados;
    }

    public Long getTotalLotes() {
        return totalLotes;
    }

    public void setTotalLotes(Long totalLotes) {
        this.totalLotes = totalLotes;
    }

    public Long getLotesDisponibles() {
        return lotesDisponibles;
    }

    public void setLotesDisponibles(Long lotesDisponibles) {
        this.lotesDisponibles = lotesDisponibles;
    }

    public Long getLotesDefectuosos() {
        return lotesDefectuosos;
    }

    public void setLotesDefectuosos(Long lotesDefectuosos) {
        this.lotesDefectuosos = lotesDefectuosos;
    }

    public Double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(Double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public List<ProductoPopularDTO> getProductosPopulares() {
        return productosPopulares;
    }

    public void setProductosPopulares(List<ProductoPopularDTO> productosPopulares) {
        this.productosPopulares = productosPopulares;
    }

    public List<PedidoRecenteDTO> getPedidosRecientes() {
        return pedidosRecientes;
    }

    public void setPedidosRecientes(List<PedidoRecenteDTO> pedidosRecientes) {
        this.pedidosRecientes = pedidosRecientes;
    }

    // DTOs internos
    public static class ProductoPopularDTO {
        private String nombre;
        private Long cantidadVendida;

        public ProductoPopularDTO() {}

        public ProductoPopularDTO(String nombre, Long cantidadVendida) {
            this.nombre = nombre;
            this.cantidadVendida = cantidadVendida;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Long getCantidadVendida() {
            return cantidadVendida;
        }

        public void setCantidadVendida(Long cantidadVendida) {
            this.cantidadVendida = cantidadVendida;
        }
    }

    public static class PedidoRecenteDTO {
        private Long idPedido;
        private String numeroPedido;
        private String clienteNombre;
        private String estado;
        private Double total;
        private String fechaPedido;

        public PedidoRecenteDTO() {}

        public PedidoRecenteDTO(Long idPedido, String numeroPedido, String clienteNombre,
                                String estado, Double total, String fechaPedido) {
            this.idPedido = idPedido;
            this.numeroPedido = numeroPedido;
            this.clienteNombre = clienteNombre;
            this.estado = estado;
            this.total = total;
            this.fechaPedido = fechaPedido;
        }

        public Long getIdPedido() {
            return idPedido;
        }

        public void setIdPedido(Long idPedido) {
            this.idPedido = idPedido;
        }

        public String getNumeroPedido() {
            return numeroPedido;
        }

        public void setNumeroPedido(String numeroPedido) {
            this.numeroPedido = numeroPedido;
        }

        public String getClienteNombre() {
            return clienteNombre;
        }

        public void setClienteNombre(String clienteNombre) {
            this.clienteNombre = clienteNombre;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public String getFechaPedido() {
            return fechaPedido;
        }

        public void setFechaPedido(String fechaPedido) {
            this.fechaPedido = fechaPedido;
        }
    }
}
