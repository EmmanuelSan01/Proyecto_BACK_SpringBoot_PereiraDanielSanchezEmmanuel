package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consultas SIN lotes para evitar referencias circulares
    @Query("SELECT p FROM Producto p")
    List<Producto> findAllWithoutLotes();

    @Query("SELECT p FROM Producto p WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithoutLotes(@Param("id") Long id);

    @Query("SELECT p FROM Producto p WHERE p.conservante = :conservante")
    List<Producto> findByConservanteWithoutLotes(@Param("conservante") Producto.TipoConservante conservante);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> findByNombreContainingIgnoreCaseWithoutLotes(@Param("nombre") String nombre);

    // Consultas originales (mantener para compatibilidad)
    List<Producto> findByConservante(Producto.TipoConservante conservante);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    Optional<Producto> findByCodigoSku(String codigoSku);
}
