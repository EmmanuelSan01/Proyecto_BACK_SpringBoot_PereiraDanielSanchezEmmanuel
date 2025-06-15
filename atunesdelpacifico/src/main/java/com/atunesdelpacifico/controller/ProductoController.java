package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/operador/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Listar todos los productos", description = "Obtiene el catálogo completo de productos")
    public ResponseEntity<ApiResponse<List<Producto>>> getAllProductos() {
        try {
            List<Producto> productos = productoService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener productos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ApiResponse<Producto>> getProductoById(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoService.findById(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener producto: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Crear nuevo producto", description = "Agrega un nuevo producto al catálogo")
    public ResponseEntity<ApiResponse<Producto>> createProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Producto creado exitosamente", nuevoProducto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear producto: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    public ResponseEntity<ApiResponse<Producto>> updateProducto(@PathVariable Long id, 
                                                              @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.update(id, producto);
            return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", productoActualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar producto: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo")
    public ResponseEntity<ApiResponse<String>> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar producto: " + e.getMessage()));
        }
    }
    
    @GetMapping("/conservante/{conservante}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Obtener productos por conservante", description = "Filtra productos por tipo de conservante")
    public ResponseEntity<ApiResponse<List<Producto>>> getProductosByConservante(@PathVariable Producto.TipoConservante conservante) {
        try {
            List<Producto> productos = productoService.findByConservante(conservante);
            return ResponseEntity.ok(ApiResponse.success("Productos filtrados exitosamente", productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al filtrar productos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'CLIENTE')")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto en su nombre")
    public ResponseEntity<ApiResponse<List<Producto>>> buscarProductosPorNombre(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en la búsqueda: " + e.getMessage()));
        }
    }
}
