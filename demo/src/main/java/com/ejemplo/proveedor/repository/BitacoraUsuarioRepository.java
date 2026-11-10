package com.ejemplo.proveedor.repository;

import com.ejemplo.proveedor.model.BitacoraUsuario;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BitacoraUsuarioRepository extends JpaRepository<BitacoraUsuario, Long> {

  List<BitacoraUsuario> findAllByOrderByFechaEventoDesc(Pageable pageable);
}
