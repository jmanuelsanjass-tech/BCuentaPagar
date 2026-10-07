package com.ejemplo.proveedor.repository;

import java.util.Optional;
import com.ejemplo.proveedor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByUsername(String username);
}
