package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Venta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {

  @Override
  @EntityGraph(attributePaths = "detalles")
  List<Venta> findAll();

  @EntityGraph(attributePaths = "detalles")
  Optional<Venta> findWithDetailsByVentaId(Long ventaId);
}
