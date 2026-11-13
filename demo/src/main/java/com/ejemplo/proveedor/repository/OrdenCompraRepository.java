package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.OrdenCompra;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, UUID> {

  boolean existsByEmpresaIdAndNumeroOc(UUID empresaId, String numeroOc);

  boolean existsByEmpresaIdAndNumeroOcAndIdOrdenCompraNot(
      UUID empresaId, String numeroOc, UUID idOrdenCompra);
}
