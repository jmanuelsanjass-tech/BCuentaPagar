package com.ejemplo.proveedor.repository;

import java.util.Optional;
import com.ejemplo.proveedor.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

  Optional<Rol> findByNombre(String nombre);
}
