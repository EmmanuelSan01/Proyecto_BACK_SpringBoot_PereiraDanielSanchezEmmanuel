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

    // Métodos SIN lotes para evitar referencias circulares
    public List<Producto> findAllWithoutLotes() {
        return productoRepository.findAllWithoutLotes();
    }

    public Optional<Producto> findByIdWithoutLotes(Long id) {
        return productoRepository.findByIdWithoutLotes(id);
    }

    public List<Producto> findByConservanteWithoutLotes(Producto.TipoConservante conservante) {
        return productoRepository.findByConservanteWithoutLotes(conservante);
    }

    public List<Producto> buscarPorNombreWithoutLotes(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseWithoutLotes(nombre);
    }

    // Métodos originales (mantener para compatibilidad)
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
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setIdProducto(id);
            return productoRepository.save(producto);
        }
        throw new RuntimeException("Producto no encontrado con ID: " + id);
    }

    public void deleteById(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }
}
