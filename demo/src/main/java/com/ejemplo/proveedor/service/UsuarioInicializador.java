package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UsuarioInicializador implements ApplicationRunner {

  private final UsuarioRepository usuarioRepository;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final String username;
  private final String password;

  public UsuarioInicializador(
      UsuarioRepository usuarioRepository,
      RolRepository rolRepository,
      PasswordEncoder passwordEncoder,
      @Value("${APP_SECURITY_USERNAME:}") String username,
      @Value("${APP_SECURITY_PASSWORD:}") String password) {
    this.usuarioRepository = usuarioRepository;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.username = username;
    this.password = password;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (username.isBlank() || password.isBlank()) {
      return;
    }

    Rol rolAdministrador = rolRepository.findByNombre("ADMIN")
        .or(() -> rolRepository.findByNombre("ROLE_ADMIN"))
        .orElseGet(() -> {
          Rol nuevoRol = new Rol();
          nuevoRol.setNombre("ADMIN");
          return rolRepository.save(nuevoRol);
        });

    Usuario existente = usuarioRepository.findByUsername(username).orElse(null);
    if (existente != null) {
      boolean actualizado = false;
      if (!passwordEncoder.matches(password, existente.getPassword())) {
        existente.setPassword(passwordEncoder.encode(password));
        actualizado = true;
      }
      if (existente.getRoles().add(rolAdministrador)) {
        actualizado = true;
      }
      if (actualizado) {
        usuarioRepository.save(existente);
      }
      return;
    }

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(password));
    usuario.setActivo(true);
    usuario.getRoles().add(rolAdministrador);
    usuarioRepository.save(usuario);
  }
}
