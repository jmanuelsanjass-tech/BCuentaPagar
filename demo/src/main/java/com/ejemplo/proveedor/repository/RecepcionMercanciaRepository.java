package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.RecepcionMercancia;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecepcionMercanciaRepository extends JpaRepository<RecepcionMercancia, UUID> {

  boolean existsByEmpresaIdAndNumeroRecepcion(UUID empresaId, String numeroRecepcion);

  boolean existsByEmpresaIdAndNumeroRecepcionAndIdNot(
      UUID empresaId, String numeroRecepcion, UUID id);

  boolean existsByOrdenCompra_IdOrdenCompra(UUID ordenCompraId);
}
