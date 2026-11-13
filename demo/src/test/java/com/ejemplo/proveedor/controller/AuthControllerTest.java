package com.ejemplo.proveedor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.LoginRequest;
import com.ejemplo.proveedor.dto.LoginResponse;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;

class AuthControllerTest {

  @Test
  void loginReturnsUsernameAndRolesFromDatabase() {
    UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    RolRepository rolRepository = mock(RolRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    SecurityContextRepository securityContextRepository = mock(SecurityContextRepository.class);
    Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
        "melissa",
        null,
        List.of(
            new SimpleGrantedAuthority("ROLE_VENTAS"),
            new SimpleGrantedAuthority("ROLE_ADMIN")));
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
    Rol ventas = new Rol();
    ventas.setNombre("VENTAS");
    Rol admin = new Rol();
    admin.setNombre("ROLE_ADMIN");
    Usuario usuario = new Usuario();
    usuario.setUsername("melissa");
    usuario.setRoles(java.util.Set.of(ventas, admin));
    when(usuarioRepository.findByUsername("melissa")).thenReturn(java.util.Optional.of(usuario));

    AuthController controller = new AuthController(
        usuarioRepository,
        rolRepository,
        passwordEncoder,
        authenticationManager,
        securityContextRepository);
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("melissa");
    loginRequest.setPassword("password");
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    ResponseEntity<LoginResponse> result = controller.login(loginRequest, request, response);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("melissa", result.getBody().username());
    assertEquals(List.of("ADMIN", "VENTAS"), result.getBody().roles());
    verify(usuarioRepository).findByUsername("melissa");
    verify(securityContextRepository).saveContext(
        any(), eq((HttpServletRequest) request), eq((HttpServletResponse) response));
  }
}
