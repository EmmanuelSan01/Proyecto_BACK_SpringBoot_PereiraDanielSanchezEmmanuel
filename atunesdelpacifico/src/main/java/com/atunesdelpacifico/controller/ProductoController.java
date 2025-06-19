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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene la lista completa de productos")
    public ResponseEntity<ApiResponse<List<Producto>>> getAllProductos() {
        try {
            List<Producto> productos = productoService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener productos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ApiResponse<Producto>> getProductoById(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoService.findById(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener producto: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el catálogo")
    public ResponseEntity<ApiResponse<Producto>> createProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.save(producto);
            return ResponseEntity.ok(ApiResponse.success("Producto creado exitosamente", nuevoProducto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    public ResponseEntity<ApiResponse<Producto>> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.update(id, producto);
            return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", productoActualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
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
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar producto: " + e.getMessage()));
        }
    }

    @GetMapping("/conservante/{conservante}")
    @Operation(summary = "Buscar productos por conservante", description = "Obtiene productos filtrados por tipo de conservante")
    public ResponseEntity<ApiResponse<List<Producto>>> getProductosByConservante(@PathVariable Producto.TipoConservante conservante) {
        try {
            List<Producto> productos = productoService.findByConservante(conservante);
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener productos: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto en su nombre")
    public ResponseEntity<ApiResponse<List<Producto>>> buscarProductosPorNombre(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", productos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error en la búsqueda: " + e.getMessage()));
        }
    }
}
