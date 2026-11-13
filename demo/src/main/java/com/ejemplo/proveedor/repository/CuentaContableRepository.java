package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.CuentaContable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaContableRepository extends JpaRepository<CuentaContable, UUID> {

  List<CuentaContable> findAllByOrderByCodigoCuentaAsc();
}
