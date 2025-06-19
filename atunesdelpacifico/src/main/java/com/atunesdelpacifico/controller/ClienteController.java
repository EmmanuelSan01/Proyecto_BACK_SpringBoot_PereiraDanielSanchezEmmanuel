package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Cliente;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.model.dto.ClienteRequest;
import com.atunesdelpacifico.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista completa de clientes")
    public ResponseEntity<ApiResponse<List<Cliente>>> getAllClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @clienteService.isOwner(#id, authentication.name)")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    public ResponseEntity<ApiResponse<Cliente>> getClienteById(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Cliente encontrado", cliente.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener cliente: " + e.getMessage()));
        }
    }

    @PostMapping("/registro")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @Operation(summary = "Registrar nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    public ResponseEntity<ApiResponse<Cliente>> registrarCliente(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteService.registrarCliente(clienteRequest);
            return ResponseEntity.ok(ApiResponse.success("Cliente registrado exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al registrar cliente: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR') or @clienteService.isOwner(#id, authentication.name)")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    public ResponseEntity<ApiResponse<Cliente>> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteService.update(id, clienteRequest);
            return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al actualizar cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar cliente", description = "Cambia el estado del cliente a inactivo")
    public ResponseEntity<ApiResponse<String>> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar cliente: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Buscar clientes por estado", description = "Obtiene clientes filtrados por estado")
    public ResponseEntity<ApiResponse<List<Cliente>>> getClientesByEstado(@PathVariable Cliente.EstadoCliente estado) {
        try {
            List<Cliente> clientes = clienteService.findByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Buscar clientes por tipo", description = "Obtiene clientes filtrados por tipo")
    public ResponseEntity<ApiResponse<List<Cliente>>> getClientesByTipo(@PathVariable Cliente.TipoCliente tipo) {
        try {
            List<Cliente> clientes = clienteService.findByTipoCliente(tipo);
            return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Buscar clientes por nombre", description = "Busca clientes que contengan el texto en su nombre")
    public ResponseEntity<ApiResponse<List<Cliente>>> buscarClientesPorNombre(@RequestParam String nombre) {
        try {
            List<Cliente> clientes = clienteService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", clientes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error en la búsqueda: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar estado del cliente", description = "Cambia el estado de un cliente")
    public ResponseEntity<ApiResponse<Cliente>> cambiarEstadoCliente(@PathVariable Long id, @RequestParam Cliente.EstadoCliente estado) {
        try {
            Cliente cliente = clienteService.cambiarEstado(id, estado);
            return ResponseEntity.ok(ApiResponse.success("Estado del cliente actualizado", cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage()));
        }
    }

    @GetMapping("/perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Obtener perfil del cliente autenticado", description = "Obtiene el perfil del cliente que está autenticado")
    public ResponseEntity<ApiResponse<Cliente>> getPerfilCliente(Authentication authentication) {
        try {
            Optional<Cliente> cliente = clienteService.findByUsuarioNombre(authentication.getName());
            if (cliente.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", cliente.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener perfil: " + e.getMessage()));
        }
    }
}
