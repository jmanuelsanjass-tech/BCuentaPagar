package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.CuentaPorCobrarHistoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaPorCobrarHistoriaRepository
    extends JpaRepository<CuentaPorCobrarHistoria, Long> {

  @Override
  @EntityGraph(attributePaths = "cuentaPorCobrar")
  List<CuentaPorCobrarHistoria> findAll();

  @Override
  @EntityGraph(attributePaths = "cuentaPorCobrar")
  Optional<CuentaPorCobrarHistoria> findById(Long idHistoria);
}
