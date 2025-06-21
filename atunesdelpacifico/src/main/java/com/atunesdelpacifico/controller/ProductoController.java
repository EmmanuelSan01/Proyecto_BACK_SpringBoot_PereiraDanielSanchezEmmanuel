package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestión de productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ENDPOINTS PÚBLICOS (sin autenticación requerida)
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene la lista completa de productos SIN lotes para evitar referencias circulares")
    public ResponseEntity<ApiResponse<List<Producto>>> getAllProductos() {
        try {
            // Obtener productos sin lotes para evitar referencias circulares
            List<Producto> productos = productoService.findAllWithoutLotes();
            ApiResponse<List<Producto>> response = ApiResponse.success("Productos obtenidos exitosamente", productos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<Producto>> errorResponse = ApiResponse.error("Error al obtener productos: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ApiResponse<Producto>> getProductoById(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoService.findByIdWithoutLotes(id);
            if (producto.isPresent()) {
                ApiResponse<Producto> response = ApiResponse.success("Producto encontrado", producto.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Producto> response = ApiResponse.error("Producto no encontrado");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            ApiResponse<Producto> errorResponse = ApiResponse.error("Error al obtener producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/conservante/{conservante}")
    @Operation(summary = "Buscar productos por conservante", description = "Obtiene productos filtrados por tipo de conservante")
    public ResponseEntity<ApiResponse<List<Producto>>> getProductosByConservante(@PathVariable Producto.TipoConservante conservante) {
        try {
            List<Producto> productos = productoService.findByConservanteWithoutLotes(conservante);
            ApiResponse<List<Producto>> response = ApiResponse.success("Productos obtenidos exitosamente", productos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<Producto>> errorResponse = ApiResponse.error("Error al obtener productos: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto en su nombre")
    public ResponseEntity<ApiResponse<List<Producto>>> buscarProductosPorNombre(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.buscarPorNombreWithoutLotes(nombre);
            ApiResponse<List<Producto>> response = ApiResponse.success("Búsqueda completada", productos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<Producto>> errorResponse = ApiResponse.error("Error en la búsqueda: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ENDPOINTS QUE REQUIEREN AUTENTICACIÓN
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el catálogo")
    public ResponseEntity<ApiResponse<Producto>> createProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.save(producto);
            ApiResponse<Producto> response = ApiResponse.success("Producto creado exitosamente", nuevoProducto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Producto> errorResponse = ApiResponse.error("Error al crear producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    public ResponseEntity<ApiResponse<Producto>> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.update(id, producto);
            ApiResponse<Producto> response = ApiResponse.success("Producto actualizado exitosamente", productoActualizado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Producto> errorResponse = ApiResponse.error("Error al actualizar producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo")
    public ResponseEntity<ApiResponse<String>> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteById(id);
            ApiResponse<String> response = ApiResponse.success("Producto eliminado exitosamente", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = ApiResponse.error("Error al eliminar producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
