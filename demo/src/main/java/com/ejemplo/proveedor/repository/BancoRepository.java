package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Banco;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, UUID> {

  boolean existsByCodigoBanco(String codigoBanco);

  boolean existsByCodigoBancoAndIdNot(String codigoBanco, UUID id);
}
