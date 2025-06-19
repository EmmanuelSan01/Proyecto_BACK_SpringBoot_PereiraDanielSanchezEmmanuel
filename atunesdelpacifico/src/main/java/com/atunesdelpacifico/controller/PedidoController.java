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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene la lista completa de pedidos")
    public ResponseEntity<ApiResponse<List<Pedido>>> getAllPedidos() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @pedidoService.isOwner(#id, authentication.name)")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene un pedido específico por su ID")
    public ResponseEntity<ApiResponse<Pedido>> getPedidoById(@PathVariable Long id) {
        try {
            Optional<Pedido> pedido = pedidoService.findById(id);
            if (pedido.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", pedido.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedido: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Crear nuevo pedido", description = "Crea un nuevo pedido")
    public ResponseEntity<ApiResponse<Pedido>> createPedido(@Valid @RequestBody PedidoRequest pedidoRequest, Authentication authentication) {
        try {
            Pedido pedido = pedidoService.crearPedido(pedidoRequest, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Pedido creado exitosamente", pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al crear pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Actualizar pedido", description = "Actualiza los datos de un pedido existente")
    public ResponseEntity<ApiResponse<Pedido>> updatePedido(@PathVariable Long id, @Valid @RequestBody PedidoRequest pedidoRequest) {
        try {
            Pedido pedido = pedidoService.update(id, pedidoRequest);
            return ResponseEntity.ok(ApiResponse.success("Pedido actualizado exitosamente", pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al actualizar pedido: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido del sistema")
    public ResponseEntity<ApiResponse<String>> deletePedido(@PathVariable Long id) {
        try {
            pedidoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Pedido eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @pedidoService.isClienteOwner(#clienteId, authentication.name)")
    @Operation(summary = "Obtener pedidos por cliente", description = "Obtiene todos los pedidos de un cliente específico")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByCliente(@PathVariable Long clienteId) {
        try {
            List<Pedido> pedidos = pedidoService.findByClienteId(clienteId);
            return ResponseEntity.ok(ApiResponse.success("Pedidos del cliente obtenidos", pedidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Obtener pedidos del cliente autenticado", description = "Obtiene todos los pedidos del cliente que está autenticado")
    public ResponseEntity<ApiResponse<List<Pedido>>> getMisPedidos(Authentication authentication) {
        try {
            List<Pedido> pedidos = pedidoService.findByUsuarioNombre(authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Mis pedidos obtenidos", pedidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Buscar pedidos por estado", description = "Obtiene pedidos filtrados por estado")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByEstado(@PathVariable Pedido.EstadoPedido estado) {
        try {
            List<Pedido> pedidos = pedidoService.findByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Buscar pedidos por fecha", description = "Obtiene pedidos en un rango de fechas")
    public ResponseEntity<ApiResponse<List<Pedido>>> getPedidosByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Pedido> pedidos = pedidoService.findByFechaPedido(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Cambiar estado del pedido", description = "Cambia el estado de un pedido")
    public ResponseEntity<ApiResponse<Pedido>> cambiarEstadoPedido(@PathVariable Long id, @RequestParam Pedido.EstadoPedido estado) {
        try {
            Pedido pedido = pedidoService.cambiarEstado(id, estado);
            return ResponseEntity.ok(ApiResponse.success("Estado del pedido actualizado", pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Confirmar pedido", description = "Confirma un pedido y actualiza el inventario")
    public ResponseEntity<ApiResponse<Pedido>> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(id);
            return ResponseEntity.ok(ApiResponse.success("Pedido confirmado exitosamente", pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al confirmar pedido: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @pedidoService.isOwner(#id, authentication.name)")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido y restaura el inventario")
    public ResponseEntity<ApiResponse<Pedido>> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(ApiResponse.success("Pedido cancelado exitosamente", pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al cancelar pedido: " + e.getMessage()));
        }
    }
}
