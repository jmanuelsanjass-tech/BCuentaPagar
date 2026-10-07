package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioInicializadorTest {

  @Test
  void updatesPasswordForAnExistingConfiguredUser() {
    UsuarioRepository usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
    RolRepository rolRepository = org.mockito.Mockito.mock(RolRepository.class);
    PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
    Usuario usuario = new Usuario();
    usuario.setUsername("admin");
    usuario.setPassword("old-hash");

    when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
    when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

    UsuarioInicializador inicializador =
        new UsuarioInicializador(usuarioRepository, rolRepository, passwordEncoder,
            "admin", "new-password");
    inicializador.run(new DefaultApplicationArguments());

    assertEquals("new-hash", usuario.getPassword());
    verify(usuarioRepository).save(usuario);
  }
}
