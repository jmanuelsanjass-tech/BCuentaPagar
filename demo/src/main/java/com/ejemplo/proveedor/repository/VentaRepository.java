package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Venta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {

  @Override
  @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
  List<Venta> findAll();

  @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
  Optional<Venta> findWithDetailsByIdVenta(Long idVenta);
}
