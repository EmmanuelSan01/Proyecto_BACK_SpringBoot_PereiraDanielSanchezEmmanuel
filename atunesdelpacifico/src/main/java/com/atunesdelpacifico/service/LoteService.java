package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.entity.Producto;
import com.atunesdelpacifico.repository.LoteRepository;
import com.atunesdelpacifico.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoteService {
    
    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Lote> findAll() {
        return loteRepository.findAll();
    }
    
    public Optional<Lote> findById(Long id) {
        return loteRepository.findById(id);
    }
    
    public List<Lote> findDisponibles() {
        return loteRepository.findDisponiblesOrderByFechaProduccion();
    }
    
    public List<Lote> findByEstado(Lote.EstadoLote estado) {
        return loteRepository.findByEstado(estado);
    }
    
    public Lote save(Lote lote) {
        // Verificar que el producto existe
        if (lote.getProducto() != null && lote.getProducto().getIdProducto() != null) {
            Producto producto = productoRepository.findById(lote.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            lote.setProducto(producto);
        }
        
        return loteRepository.save(lote);
    }
    
    public Lote update(Long id, Lote loteActualizado) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        
        lote.setFechaProduccion(loteActualizado.getFechaProduccion());
        lote.setCantidadTotal(loteActualizado.getCantidadTotal());
        lote.setCantidadDisponible(loteActualizado.getCantidadDisponible());
        lote.setEstado(loteActualizado.getEstado());
        
        if (loteActualizado.getProducto() != null && loteActualizado.getProducto().getIdProducto() != null) {
            Producto producto = productoRepository.findById(loteActualizado.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            lote.setProducto(producto);
        }
        
        return loteRepository.save(lote);
    }
    
    public void deleteById(Long id) {
        if (!loteRepository.existsById(id)) {
            throw new RuntimeException("Lote no encontrado");
        }
        loteRepository.deleteById(id);
    }
    
    public List<Lote> findByFechaProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        return loteRepository.findByFechaProduccionBetween(fechaInicio, fechaFin);
    }
    
    public Lote cambiarEstado(Long id, Lote.EstadoLote nuevoEstado) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        
        lote.setEstado(nuevoEstado);
        return loteRepository.save(lote);
    }
    
    public boolean verificarDisponibilidad(Long loteId, Integer cantidadRequerida) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        
        return lote.getEstado() == Lote.EstadoLote.DISPONIBLE && 
               lote.getCantidadDisponible() >= cantidadRequerida;
    }
    
    public void reducirCantidadDisponible(Long loteId, Integer cantidad) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        
        if (lote.getCantidadDisponible() < cantidad) {
            throw new RuntimeException("Cantidad insuficiente en el lote");
        }
        
        lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidad);
        
        // Si se agota el lote, cambiar estado
        if (lote.getCantidadDisponible() == 0) {
            lote.setEstado(Lote.EstadoLote.VENDIDO);
        }
        
        loteRepository.save(lote);
    }
}
