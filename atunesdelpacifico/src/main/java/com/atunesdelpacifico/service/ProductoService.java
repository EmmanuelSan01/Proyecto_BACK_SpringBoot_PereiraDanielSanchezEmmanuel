package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByConservante(Producto.TipoConservante conservante) {
        return productoRepository.findByConservante(conservante);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Producto save(Producto producto) {
        // Verificar que el código SKU no exista
        if (productoRepository.existsByCodigoSku(producto.getCodigoSku())) {
            throw new RuntimeException("El código SKU ya existe");
        }

        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar código SKU si cambió
        if (!producto.getCodigoSku().equals(productoActualizado.getCodigoSku())) {
            if (productoRepository.existsByCodigoSku(productoActualizado.getCodigoSku())) {
                throw new RuntimeException("El código SKU ya existe");
            }
        }

        producto.setCodigoSku(productoActualizado.getCodigoSku());
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setConservante(productoActualizado.getConservante());
        producto.setContenidoG(productoActualizado.getContenidoG());
        producto.setPrecioLista(productoActualizado.getPrecioLista());
        producto.setPrecioCosto(productoActualizado.getPrecioCosto());
        producto.setStockMinimo(productoActualizado.getStockMinimo());

        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }
}
