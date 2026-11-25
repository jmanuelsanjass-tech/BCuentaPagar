package com.ejemplo.proveedor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.LoginRequest;
import com.ejemplo.proveedor.dto.LoginResponse;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import com.ejemplo.proveedor.security.JwtService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {

    @Test
    void loginReturnsUsernameAndRolesFromDatabase() {

        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        RolRepository rolRepository = mock(RolRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        Authentication authenticated =
                UsernamePasswordAuthenticationToken.authenticated(
                        "melissa",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_VENTAS")
                        ));

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticated);

        UserDetails userDetails =
                User.withUsername("melissa")
                        .password("x")
                        .authorities("ROLE_ADMIN")
                        .build();

        when(userDetailsService.loadUserByUsername("melissa"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("fake-jwt");

        Rol ventas = new Rol();
        ventas.setNombre("VENTAS");

        Rol admin = new Rol();
        admin.setNombre("ROLE_ADMIN");

        Usuario usuario = new Usuario();
        usuario.setUsername("melissa");
        usuario.setRoles(Set.of(ventas, admin));

        when(usuarioRepository.findByUsername("melissa"))
                .thenReturn(Optional.of(usuario));

        AuthController controller =
                new AuthController(
                        usuarioRepository,
                        rolRepository,
                        passwordEncoder,
                        authenticationManager,
                        jwtService,
                        userDetailsService);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("melissa");
        loginRequest.setPassword("password");

        ResponseEntity<?> response =
                controller.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        LoginResponse body = (LoginResponse) response.getBody();

        assertEquals("melissa", body.username());
        assertEquals("fake-jwt", body.token());
        assertEquals(List.of("ADMIN", "VENTAS"), body.roles());
    }
}