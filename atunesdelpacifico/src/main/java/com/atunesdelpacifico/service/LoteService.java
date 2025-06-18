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

        lote.setFechaProd(loteActualizado.getFechaProd()); // Corregido
        lote.setCantidadTotal(loteActualizado.getCantidadTotal());
        lote.setCantidadDisp(loteActualizado.getCantidadDisp()); // Corregido
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

        return lote.getEstado() == Lote.EstadoLote.DISPONIBLE && // Corregido
                lote.getCantidadDisp() >= cantidadRequerida; // Corregido
    }

    public void reducirCantidadDisponible(Long loteId, Integer cantidad) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        if (lote.getCantidadDisp() < cantidad) { // Corregido
            throw new RuntimeException("Cantidad insuficiente en el lote");
        }

        lote.setCantidadDisp(lote.getCantidadDisp() - cantidad); // Corregido

        if (lote.getCantidadDisp() == 0) { // Corregido
            lote.setEstado(Lote.EstadoLote.VENDIDO); // Corregido
        }

        loteRepository.save(lote);
    }
}
