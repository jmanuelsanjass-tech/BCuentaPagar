package com.ejemplo.proveedor.controller;

import java.util.Locale;
import java.util.List;
import java.util.Set;
import com.ejemplo.proveedor.dto.LoginRequest;
import com.ejemplo.proveedor.dto.LoginResponse; // Asegúrate de que este DTO tenga el campo "token"
import com.ejemplo.proveedor.dto.RegistroRequest;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import com.ejemplo.proveedor.security.JwtService;
import com.ejemplo.proveedor.security.SecurityRoles;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
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
  private final JwtService jwtService; // NUEVO SERVICIO
  private final UserDetailsService userDetailsService; // NUEVO SERVICIO

  public AuthController(
      UsuarioRepository usuarioRepository,
      RolRepository rolRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      UserDetailsService userDetailsService) {
    this.usuarioRepository = usuarioRepository;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  // Ya no necesitamos el endpoint /csrf
  // @GetMapping("/csrf") ...

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
      // 1. Autenticar
      Authentication authentication = authenticationManager.authenticate(
          UsernamePasswordAuthenticationToken.unauthenticated(
              request.getUsername().trim(), request.getPassword()));

      // 2. Cargar UserDetails y generar Token
      UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
      String jwt = jwtService.generateToken(userDetails);

      // 3. Obtener roles para enviarlos al frontend
      Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElseThrow();
      List<String> roles = usuario.getRoles().stream()
          .map(rol -> SecurityRoles.normalizeName(rol.getNombre()))
          .distinct()
          .sorted()
          .toList();

      // 4. Devolver el token y los roles (Asegúrate de que LoginResponse acepte estos
      // campos)
      return ResponseEntity.ok(
          new LoginResponse(
              authentication.getName(),
              roles,
              jwt));

    } catch (AuthenticationException exception) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // Al usar JWT, el logout se maneja en el frontend borrando el token del
    // localStorage.
    // El backend solo responde OK.
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