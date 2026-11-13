package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.OrdenCompraLinea;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCompraLineaRepository extends JpaRepository<OrdenCompraLinea, UUID> {

  List<OrdenCompraLinea> findAllByOrdenCompra_IdOrdenCompraOrderByNumeroLineaAsc(
      UUID ordenCompraId);

  Optional<OrdenCompraLinea> findByOrdenCompra_IdOrdenCompraAndIdOrdenCompraLinea(
      UUID ordenCompraId, UUID idOrdenCompraLinea);

  boolean existsByOrdenCompra_IdOrdenCompraAndNumeroLinea(
      UUID ordenCompraId, Integer numeroLinea);

  boolean existsByOrdenCompra_IdOrdenCompraAndNumeroLineaAndIdOrdenCompraLineaNot(
      UUID ordenCompraId, Integer numeroLinea, UUID idOrdenCompraLinea);

  boolean existsByOrdenCompra_IdOrdenCompra(UUID ordenCompraId);
}
