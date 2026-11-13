package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class UserDetailsServiceImplTest {

  @Test
  void roleWithPrefixProducesExpectedAdminAuthority() {
    UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    Usuario usuario = new Usuario();
    usuario.setUsername("admin");
    Rol rolAdministrador = new Rol();
    rolAdministrador.setNombre("ROLE_ADMIN");
    usuario.setRoles(Set.of(rolAdministrador));
    when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

    UserDetailsServiceImpl service = new UserDetailsServiceImpl(usuarioRepository);

    UserDetails userDetails = service.loadUserByUsername("admin");

    assertEquals(Set.of("ROLE_ADMIN"), userDetails.getAuthorities().stream()
        .map(authority -> authority.getAuthority())
        .collect(java.util.stream.Collectors.toSet()));
  }
}
