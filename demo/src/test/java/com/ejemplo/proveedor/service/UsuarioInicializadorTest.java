package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioInicializadorTest {

  @Test
  void skipsInitializationWhenCredentialsAreMissing() {
    UsuarioRepository usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
    RolRepository rolRepository = org.mockito.Mockito.mock(RolRepository.class);
    PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);

    UsuarioInicializador inicializador =
        new UsuarioInicializador(usuarioRepository, rolRepository, passwordEncoder, "", "");
    inicializador.run(new DefaultApplicationArguments());

    verifyNoInteractions(usuarioRepository, rolRepository, passwordEncoder);
  }

  @Test
  void updatesPasswordForAnExistingConfiguredUser() {
    UsuarioRepository usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
    RolRepository rolRepository = org.mockito.Mockito.mock(RolRepository.class);
    PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
    Usuario usuario = new Usuario();
    usuario.setUsername("admin");
    usuario.setPassword("old-hash");
    Rol rolAdministrador = new Rol();
    rolAdministrador.setNombre("ADMIN");

    when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
    when(rolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rolAdministrador));
    when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
    when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

    UsuarioInicializador inicializador =
        new UsuarioInicializador(usuarioRepository, rolRepository, passwordEncoder,
            "admin", "new-password");
    inicializador.run(new DefaultApplicationArguments());

    assertEquals("new-hash", usuario.getPassword());
    assertTrue(usuario.getRoles().contains(rolAdministrador));
    verify(usuarioRepository).save(usuario);
  }

  @Test
  void createsConfiguredUserWithAdminRole() {
    UsuarioRepository usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
    RolRepository rolRepository = org.mockito.Mockito.mock(RolRepository.class);
    PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
    Rol rolAdministrador = new Rol();
    rolAdministrador.setNombre("ADMIN");

    when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
    when(rolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rolAdministrador));
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");

    UsuarioInicializador inicializador =
        new UsuarioInicializador(usuarioRepository, rolRepository, passwordEncoder,
            "admin", "password");
    inicializador.run(new DefaultApplicationArguments());

    org.mockito.ArgumentCaptor<Usuario> usuarioCaptor =
        org.mockito.ArgumentCaptor.forClass(Usuario.class);
    verify(usuarioRepository).save(usuarioCaptor.capture());
    Usuario creado = usuarioCaptor.getValue();
    assertEquals("admin", creado.getUsername());
    assertTrue(creado.getRoles().contains(rolAdministrador));
  }
}
