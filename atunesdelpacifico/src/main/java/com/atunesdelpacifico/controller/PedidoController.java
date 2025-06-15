package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.PedidoRequest;
import com.atunesdelpacifico.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos de clientes")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene la lista completa de pedidos")
    public ResponseEntity<ApiResponse<List<Pedido>>> getAllPedidos() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene un pedido específico por su ID")
    public ResponseEntity<ApiResponse<Pedido>> getPedidoById(@PathVariable Long id) {
        try {
            Optional<Pedido> pedido = pedidoService.findById(id);
            if (pedido.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", pedido.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Pedido no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pedido: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Crear nuevo pedido", description = "Crea un nuevo pedido con sus detalles")
    public ResponseEntity<ApiResponse<Pedido>> createPedido(@Valid @RequestBody PedidoRequest pedidoRequest) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pedido creado exitosamente", nuevoPedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear pedido: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Cambiar estado del pedido", description = "Actualiza el estado de un pedido")
    public ResponseEntity<ApiResponse<Pedido>> cambiarEstadoPedido(@PathVariable Long id, 
                                                                  @RequestParam Pedido.EstadoPedido estado) {
        try {
            Pedido pedido = pedidoService.cambiarEstado(id, estado);
            return ResponseEntity.ok(ApiResponse.success("Estado del pedido actualizado", pedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido y restaura el inventario")
    public ResponseEntity<ApiResponse<String>> cancelarPedido(@PathVariable Long id) {
        try {
            pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(ApiResponse.success("Pedido cancelado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cancelar pedido: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener pedidos por cliente", description = "Lista todos los pedidos de un cliente específico")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByCliente(@PathVariable Long clienteId) {
        try {
            List<Pedido> pedidos = pedidoService.findByCliente(clienteId);
            return ResponseEntity.ok(ApiResponse.success("Pedidos del cliente obtenidos", pedidos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pedidos del cliente: " + e.getMessage()));
        }
    }
    
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener pedidos por estado", description = "Filtra pedidos por su estado")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByEstado(@PathVariable Pedido.EstadoPedido estado) {
        try {
            List<Pedido> pedidos = pedidoService.findByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Pedidos filtrados exitosamente", pedidos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al filtrar pedidos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener pedidos por rango de fechas", description = "Filtra pedidos por fecha de pedido")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Pedido> pedidos = pedidoService.findByFechaPedido(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Pedidos filtrados por fecha", pedidos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al filtrar pedidos por fecha: " + e.getMessage()));
        }
    }
    
    @GetMapping("/estadisticas/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Contar pedidos por estado", description = "Obtiene el número total de pedidos en un estado específico")
    public ResponseEntity<ApiResponse<Long>> contarPedidosPorEstado(@PathVariable Pedido.EstadoPedido estado) {
        try {
            Long count = pedidoService.countByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Conteo completado", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al contar pedidos: " + e.getMessage()));
        }
    }
}
