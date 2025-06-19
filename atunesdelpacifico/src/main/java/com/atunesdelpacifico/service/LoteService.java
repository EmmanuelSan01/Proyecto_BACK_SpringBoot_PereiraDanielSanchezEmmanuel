package com.atunesdelpacifico.service;

import com.atunesdelpacifico.entity.Lote;
import com.atunesdelpacifico.repository.LoteRepository;
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

    public List<Lote> findAll() {
        return loteRepository.findAll();
    }

    public Optional<Lote> findById(Long id) {
        return loteRepository.findById(id);
    }

    public List<Lote> findDisponibles() {
        return loteRepository.findDisponibles();
    }

    public List<Lote> findByEstado(Lote.EstadoLote estado) {
        return loteRepository.findByEstado(estado);
    }

    public List<Lote> findByFechaProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        return loteRepository.findByFechaProdBetween(fechaInicio, fechaFin);
    }

    public Lote save(Lote lote) {
        // Generar código de lote si no existe
        if (lote.getCodigoLote() == null || lote.getCodigoLote().isEmpty()) {
            lote.setCodigoLote(generarCodigoLote());
        }

        // Verificar que el código no exista
        if (loteRepository.existsByCodigoLote(lote.getCodigoLote())) {
            throw new RuntimeException("El código de lote ya existe");
        }

        return loteRepository.save(lote);
    }

    public Lote update(Long id, Lote loteActualizado) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        lote.setFechaProd(loteActualizado.getFechaProd());
        lote.setFechaVenc(loteActualizado.getFechaVenc());
        lote.setCantidadTotal(loteActualizado.getCantidadTotal());
        lote.setCantidadDisp(loteActualizado.getCantidadDisp());
        lote.setUbicacion(loteActualizado.getUbicacion());
        lote.setLoteProveedor(loteActualizado.getLoteProveedor());

        return loteRepository.save(lote);
    }

    public void deleteById(Long id) {
        if (!loteRepository.existsById(id)) {
            throw new RuntimeException("Lote no encontrado");
        }
        loteRepository.deleteById(id);
    }

    public Lote cambiarEstado(Long id, Lote.EstadoLote estado) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        lote.setEstado(estado);
        return loteRepository.save(lote);
    }

    public boolean verificarDisponibilidad(Long loteId, Integer cantidad) {
        Optional<Lote> loteOpt = loteRepository.findById(loteId);
        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            return lote.getEstado() == Lote.EstadoLote.DISPONIBLE &&
                    lote.getCantidadDisp() >= cantidad;
        }
        return false;
    }

    public void reducirCantidadDisponible(Long loteId, Integer cantidad) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        if (lote.getCantidadDisp() < cantidad) {
            throw new RuntimeException("Cantidad insuficiente en el lote");
        }

        lote.setCantidadDisp(lote.getCantidadDisp() - cantidad);

        // Si se agotó el lote, cambiar estado
        if (lote.getCantidadDisp() == 0) {
            lote.setEstado(Lote.EstadoLote.VENDIDO);
        }

        loteRepository.save(lote);
    }

    private String generarCodigoLote() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "LOTE-" + timestamp.substring(timestamp.length() - 8);
    }
}
