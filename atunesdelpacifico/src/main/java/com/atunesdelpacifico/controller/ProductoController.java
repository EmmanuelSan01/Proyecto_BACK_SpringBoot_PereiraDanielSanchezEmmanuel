package com.atunesdelpacifico.controller;

import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.model.dto.ApiResponse;
import com.atunesdelpacifico.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Endpoints para gestión de productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene la lista de todos los productos")
    public ResponseEntity<ApiResponse<List<Producto>>> listarProductos() {
        try {
            logger.info("Solicitando lista de productos");
            List<Producto> productos = productoService.findAll();
            logger.info("Se encontraron {} productos", productos.size());
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            logger.error("Error al obtener productos: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener productos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ApiResponse<Producto>> obtenerProducto(@PathVariable Long id) {
        try {
            logger.info("Solicitando producto con ID: {}", id);
            Producto producto = productoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
            return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", producto));
        } catch (Exception e) {
            logger.error("Error al obtener producto con ID {}: ", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al obtener producto: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el sistema")
    public ResponseEntity<ApiResponse<Producto>> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            logger.info("Creando nuevo producto: {}", producto.getNombre());
            Producto nuevoProducto = productoService.save(producto);
            return ResponseEntity.ok(ApiResponse.success("Producto creado exitosamente", nuevoProducto));
        } catch (Exception e) {
            logger.error("Error al crear producto: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    @Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto")
    public ResponseEntity<ApiResponse<Producto>> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            logger.info("Actualizando producto con ID: {}", id);
            Producto productoActualizado = productoService.update(id, producto);
            return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", productoActualizado));
        } catch (Exception e) {
            logger.error("Error al actualizar producto con ID {}: ", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al actualizar producto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    public ResponseEntity<ApiResponse<String>> eliminarProducto(@PathVariable Long id) {
        try {
            logger.info("Eliminando producto con ID: {}", id);
            productoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
        } catch (Exception e) {
            logger.error("Error al eliminar producto con ID {}: ", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al eliminar producto: " + e.getMessage()));
        }
    }

    @GetMapping("/conservante/{conservante}")
    @Operation(summary = "Buscar productos por conservante", description = "Obtiene productos filtrados por tipo de conservante")
    public ResponseEntity<ApiResponse<List<Producto>>> buscarPorConservante(@PathVariable Producto.TipoConservante conservante) {
        try {
            logger.info("Buscando productos por conservante: {}", conservante);
            List<Producto> productos = productoService.findByConservante(conservante);
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            logger.error("Error al buscar productos por conservante {}: ", conservante, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al buscar productos: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto especificado en el nombre")
    public ResponseEntity<ApiResponse<List<Producto>>> buscarPorNombre(@RequestParam String nombre) {
        try {
            logger.info("Buscando productos por nombre: {}", nombre);
            List<Producto> productos = productoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Productos encontrados exitosamente", productos));
        } catch (Exception e) {
            logger.error("Error al buscar productos por nombre '{}': ", nombre, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al buscar productos: " + e.getMessage()));
        }
    }
}
