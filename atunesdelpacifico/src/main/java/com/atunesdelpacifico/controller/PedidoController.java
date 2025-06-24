package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Pedido;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.PedidoRequest;
import com.atunesdelpacifico.model.dto.PedidoResponse;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene la lista completa de pedidos")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> getAllPedidos() {
        try {
            List<PedidoResponse> pedidos = pedidoService.findAllAsResponse();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            System.err.println("Error en getAllPedidos: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @pedidoService.isOwner(#id, authentication.name)")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene un pedido específico por su ID")
    public ResponseEntity<ApiResponse<PedidoResponse>> getPedidoById(@PathVariable Long id) {
        try {
            Optional<PedidoResponse> pedido = pedidoService.findByIdAsResponse(id);
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
    public ResponseEntity<ApiResponse<PedidoResponse>> createPedido(@Valid @RequestBody PedidoRequest pedidoRequest, Authentication authentication) {
        try {
            System.out.println("=== CREAR PEDIDO ===");
            System.out.println("Usuario: " + authentication.getName());
            System.out.println("Detalles: " + pedidoRequest.getDetalles().size());

            Pedido pedido = pedidoService.crearPedido(pedidoRequest, authentication.getName());
            PedidoResponse response = pedidoService.convertToResponse(pedido);

            System.out.println("Pedido creado: " + response.getNumeroPedido());

            return ResponseEntity.ok(ApiResponse.success("Pedido creado exitosamente", response));
        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error al crear pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Obtener pedidos del cliente autenticado", description = "Obtiene todos los pedidos del cliente que está autenticado")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> getMisPedidos(Authentication authentication) {
        try {
            System.out.println("=== OBTENIENDO MIS PEDIDOS ===");
            System.out.println("Usuario: " + authentication.getName());

            List<PedidoResponse> pedidos = pedidoService.findByUsuarioNombreAsResponse(authentication.getName());

            System.out.println("Pedidos encontrados: " + pedidos.size());
            for (PedidoResponse pedido : pedidos) {
                System.out.println("Pedido: " + pedido.getNumeroPedido() + " - Detalles: " +
                        (pedido.getDetalles() != null ? pedido.getDetalles().size() : 0));
            }

            return ResponseEntity.ok(ApiResponse.success("Mis pedidos obtenidos", pedidos));
        } catch (Exception e) {
            System.err.println("Error al obtener mis pedidos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Cambiar estado del pedido", description = "Cambia el estado de un pedido")
    public ResponseEntity<ApiResponse<PedidoResponse>> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String estadoStr = request.get("estado");
            Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estadoStr);
            Pedido pedido = pedidoService.cambiarEstado(id, nuevoEstado);
            PedidoResponse response = pedidoService.convertToResponse(pedido);
            return ResponseEntity.ok(ApiResponse.success("Estado cambiado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Confirmar pedido", description = "Confirma un pedido y actualiza el inventario")
    public ResponseEntity<ApiResponse<PedidoResponse>> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(id);
            PedidoResponse response = pedidoService.convertToResponse(pedido);
            return ResponseEntity.ok(ApiResponse.success("Pedido confirmado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al confirmar pedido: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @pedidoService.isOwner(#id, authentication.name)")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido y restaura el inventario")
    public ResponseEntity<ApiResponse<PedidoResponse>> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            PedidoResponse response = pedidoService.convertToResponse(pedido);
            return ResponseEntity.ok(ApiResponse.success("Pedido cancelado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al cancelar pedido: " + e.getMessage()));
        }
    }
}
