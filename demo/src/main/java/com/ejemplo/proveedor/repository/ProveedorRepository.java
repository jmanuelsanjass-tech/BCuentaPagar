package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

  Optional<Proveedor> findByCorreo(String correo);

  boolean existsByCorreo(String correo);
}