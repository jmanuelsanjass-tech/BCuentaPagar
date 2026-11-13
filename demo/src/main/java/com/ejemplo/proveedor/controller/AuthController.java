package com.ejemplo.proveedor.controller;

import java.util.Locale;
import java.util.List;
import java.util.Set;
import com.ejemplo.proveedor.dto.LoginRequest;
import com.ejemplo.proveedor.dto.LoginResponse;
import com.ejemplo.proveedor.dto.RegistroRequest;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import com.ejemplo.proveedor.security.SecurityRoles;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UsuarioRepository usuarioRepository;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;

  public AuthController(
      UsuarioRepository usuarioRepository,
      RolRepository rolRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      SecurityContextRepository securityContextRepository) {
    this.usuarioRepository = usuarioRepository;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.securityContextRepository = securityContextRepository;
  }

  @GetMapping("/csrf")
  public CsrfToken csrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          UsernamePasswordAuthenticationToken.unauthenticated(
              request.getUsername().trim(), request.getPassword()));

      if (httpRequest.getSession(false) != null) {
        httpRequest.changeSessionId();
      }

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
      securityContextRepository.saveContext(context, httpRequest, httpResponse);
      Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
          .orElseThrow();
      List<String> roles = usuario.getRoles().stream()
          .map(rol -> SecurityRoles.normalizeName(rol.getNombre()))
          .distinct()
          .sorted()
          .toList();
      return ResponseEntity.ok(new LoginResponse(authentication.getName(), roles));
    } catch (AuthenticationException exception) {
      SecurityContextHolder.clearContext();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    new SecurityContextLogoutHandler().logout(
        httpRequest, httpResponse, SecurityContextHolder.getContext().getAuthentication());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody RegistroRequest request) {
    String username = request.getUsername().trim();
    if (usuarioRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.badRequest().body("Usuario ya existe");
    }

    String requestedRole = request.getRol().trim().toUpperCase(Locale.ROOT);
    if (!"USER".equals(requestedRole) && !"ROLE_USER".equals(requestedRole)) {
      return ResponseEntity.badRequest().body("El registro público solo permite el rol USER");
    }

    Rol rol = rolRepository.findByNombre("USER").orElse(null);
    if (rol == null) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("El rol USER no está configurado");
    }

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setActivo(true);
    usuario.setRoles(Set.of(rol));
    usuarioRepository.save(usuario);

    return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
  }
}
