package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.RolRequest;
import com.ejemplo.proveedor.dto.RolResponse;
import com.ejemplo.proveedor.model.Rol;
import com.ejemplo.proveedor.repository.RolRepository;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RolService {

  private final RolRepository rolRepository;
  private final UsuarioRepository usuarioRepository;

  public RolService(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
    this.rolRepository = rolRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional(readOnly = true)
  public List<RolResponse> listar() {
    return rolRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<RolResponse> obtener(Long id) {
    return rolRepository.findById(id).map(this::toResponse);
  }

  @Transactional
  public RolResponse crear(RolRequest request) {
    String nombre = request.nombre().trim();
    if (rolRepository.findByNombre(nombre).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre del rol ya existe");
    }

    Rol rol = new Rol();
    aplicar(rol, nombre, request);
    return toResponse(rolRepository.saveAndFlush(rol));
  }

  @Transactional
  public Optional<RolResponse> actualizar(Long id, RolRequest request) {
    return rolRepository.findById(id).map(rol -> {
      String nombre = request.nombre().trim();
      rolRepository.findByNombre(nombre)
          .filter(otroRol -> !otroRol.getIdRol().equals(id))
          .ifPresent(otroRol -> {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "El nombre del rol ya existe");
          });
      aplicar(rol, nombre, request);
      return toResponse(rolRepository.saveAndFlush(rol));
    });
  }

  @Transactional
  public boolean eliminar(Long id) {
    return rolRepository.findById(id).map(rol -> {
      if (usuarioRepository.existsByRoles_IdRol(id)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede eliminar un rol asignado a usuarios");
      }
      rolRepository.delete(rol);
      return true;
    }).orElse(false);
  }

  private void aplicar(Rol rol, String nombre, RolRequest request) {
    rol.setNombre(nombre);
    rol.setDescripcion(request.descripcion());
  }

  private RolResponse toResponse(Rol rol) {
    return new RolResponse(rol.getIdRol(), rol.getNombre(), rol.getDescripcion());
  }
}
