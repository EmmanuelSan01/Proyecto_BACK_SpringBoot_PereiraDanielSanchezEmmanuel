package com.atunesdelpacifico.repository;

import com.atunesdelpacifico.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByConservante(Producto.TipoConservante conservante);

    @Query("SELECT p FROM Producto p ORDER BY p.nombre ASC")
    List<Producto> findAllOrderByNombre();

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    boolean existsByNombre(String nombre);
}
