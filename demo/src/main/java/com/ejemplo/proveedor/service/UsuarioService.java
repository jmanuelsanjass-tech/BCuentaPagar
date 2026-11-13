package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.RolResponse;
import com.ejemplo.proveedor.dto.UsuarioCreateRequest;
import com.ejemplo.proveedor.dto.UsuarioResponse;
import com.ejemplo.proveedor.dto.UsuarioUpdateRequest;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import com.ejemplo.proveedor.security.SecurityRoles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final BitacoraUsuarioService bitacora;

  @PersistenceContext
  private EntityManager entityManager;

  public UsuarioService(
      UsuarioRepository usuarioRepository,
      RolRepository rolRepository,
      PasswordEncoder passwordEncoder,
      BitacoraUsuarioService bitacora) {
    this.usuarioRepository = usuarioRepository;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.bitacora = bitacora;
  }

  @Transactional(readOnly = true)
  public List<UsuarioResponse> listar() {
    return usuarioRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<UsuarioResponse> obtener(Long id) {
    return usuarioRepository.findById(id).map(this::toResponse);
  }

  @Transactional
  public UsuarioResponse crear(UsuarioCreateRequest request, String administrador) {
    String username = request.username().trim();
    if (usuarioRepository.findByUsername(username).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
    }

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(request.password()));
    usuario.setActivo(request.activo() == null || request.activo());
    if (request.roles() != null) {
      usuario.setRoles(resolverRoles(request.roles()));
    }

    Usuario guardado = usuarioRepository.saveAndFlush(usuario);
    entityManager.refresh(guardado);
    bitacora.registrar(
        administrador,
        guardado.getIdUsuario(),
        guardado.getUsername(),
        "USER_CREATED",
        "Usuario creado; roles: " + nombresRoles(guardado.getRoles()));
    return toResponse(guardado);
  }

  @Transactional
  public Optional<UsuarioResponse> actualizar(
      Long id, UsuarioUpdateRequest request, String administrador) {
    return usuarioRepository.findById(id).map(usuario -> {
      String username = request.username().trim();
      usuarioRepository.findByUsername(username)
          .filter(otroUsuario -> !otroUsuario.getIdUsuario().equals(id))
          .ifPresent(otroUsuario -> {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "El nombre de usuario ya existe");
          });

      String usernameAnterior = usuario.getUsername();
      boolean estadoAnterior = usuario.isActivo();
      String rolesAnteriores = nombresRoles(usuario.getRoles());
      usuario.setUsername(username);
      if (request.activo() != null) {
        impedirAutodesactivacion(usuario, request.activo(), administrador);
        usuario.setActivo(request.activo());
      }
      if (request.roles() != null) {
        usuario.setRoles(resolverRoles(request.roles()));
      }

      Usuario guardado = usuarioRepository.saveAndFlush(usuario);
      entityManager.refresh(guardado);
      if (!usernameAnterior.equals(guardado.getUsername())) {
        bitacora.registrar(
            administrador, id, guardado.getUsername(), "USER_UPDATED",
            "Nombre de usuario actualizado");
      }
      if (estadoAnterior != guardado.isActivo()) {
        registrarCambioEstado(administrador, guardado);
      }
      String rolesNuevos = nombresRoles(guardado.getRoles());
      if (!rolesAnteriores.equals(rolesNuevos)) {
        if (usernameAnterior.equalsIgnoreCase(administrador)
            && guardado.getRoles().stream().noneMatch(this::esRolAdministrador)) {
          throw new ResponseStatusException(
              HttpStatus.CONFLICT, "No puedes quitarte tu propio rol de administrador");
        }
        bitacora.registrar(
            administrador, id, guardado.getUsername(), "ROLES_CHANGED",
            "Roles: " + rolesAnteriores + " -> " + rolesNuevos);
      }
      return toResponse(guardado);
    });
  }

  @Transactional
  public Optional<UsuarioResponse> cambiarEstado(
      Long id, boolean activo, String administrador) {
    return usuarioRepository.findById(id).map(usuario -> {
      impedirAutodesactivacion(usuario, activo, administrador);
      if (usuario.isActivo() != activo) {
        usuario.setActivo(activo);
        usuario = usuarioRepository.saveAndFlush(usuario);
        registrarCambioEstado(administrador, usuario);
      }
      return toResponse(usuario);
    });
  }

  @Transactional
  public Optional<UsuarioResponse> cambiarPassword(
      Long id, String password, String administrador) {
    return usuarioRepository.findById(id).map(usuario -> {
      usuario.setPassword(passwordEncoder.encode(password));
      Usuario guardado = usuarioRepository.saveAndFlush(usuario);
      bitacora.registrar(
          administrador, id, guardado.getUsername(), "PASSWORD_CHANGED",
          "Contraseña actualizada; el valor no se registra");
      return toResponse(guardado);
    });
  }

  @Transactional
  public boolean eliminar(Long id, String administrador) {
    return usuarioRepository.findById(id).map(usuario -> {
      if (usuario.getUsername().equalsIgnoreCase(administrador)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes eliminar tu propia cuenta");
      }
      bitacora.registrar(
          administrador, id, usuario.getUsername(), "USER_DELETED", "Usuario eliminado");
      usuarioRepository.delete(usuario);
      return true;
    }).orElse(false);
  }

  private Set<Rol> resolverRoles(Set<Long> ids) {
    List<Rol> roles = rolRepository.findAllById(ids);
    if (roles.size() != ids.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o más roles no existen");
    }
    return new HashSet<>(roles);
  }

  private UsuarioResponse toResponse(Usuario usuario) {
    List<RolResponse> roles = usuario.getRoles().stream()
        .sorted((left, right) -> left.getIdRol().compareTo(right.getIdRol()))
        .map(rol -> new RolResponse(rol.getIdRol(), rol.getNombre(), rol.getDescripcion()))
        .toList();
    return new UsuarioResponse(
        usuario.getIdUsuario(),
        usuario.getUsername(),
        usuario.isActivo(),
        usuario.getFechaAlta(),
        roles);
  }

  private String nombresRoles(Set<Rol> roles) {
    return roles.stream()
        .map(Rol::getNombre)
        .sorted()
        .collect(java.util.stream.Collectors.joining(", "));
  }

  private void registrarCambioEstado(String administrador, Usuario usuario) {
    bitacora.registrar(
        administrador,
        usuario.getIdUsuario(),
        usuario.getUsername(),
        usuario.isActivo() ? "USER_ACTIVATED" : "USER_DEACTIVATED",
        usuario.isActivo() ? "Usuario activado" : "Usuario desactivado");
  }

  private void impedirAutodesactivacion(
      Usuario usuario, boolean activo, String administrador) {
    if (!activo && usuario.getUsername().equalsIgnoreCase(administrador)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "No puedes desactivar tu propia cuenta");
    }
  }

  private boolean esRolAdministrador(Rol rol) {
    return "ADMIN".equals(SecurityRoles.normalizeName(rol.getNombre()));
  }
}
