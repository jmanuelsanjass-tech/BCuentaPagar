package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.UsuarioCreateRequest;
import com.ejemplo.proveedor.dto.CambioPasswordRequest;
import com.ejemplo.proveedor.dto.EstadoUsuarioRequest;
import com.ejemplo.proveedor.dto.UsuarioResponse;
import com.ejemplo.proveedor.dto.UsuarioUpdateRequest;
import com.ejemplo.proveedor.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService service;

  public UsuarioController(UsuarioService service) {
    this.service = service;
  }

  @GetMapping
  public List<UsuarioResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<UsuarioResponse> crear(
      @Valid @RequestBody UsuarioCreateRequest request, Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.crear(request, authentication.getName()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UsuarioResponse> actualizar(
      @PathVariable Long id,
      @Valid @RequestBody UsuarioUpdateRequest request,
      Authentication authentication) {
    return service.actualizar(id, request, authentication.getName())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}/estado")
  public ResponseEntity<UsuarioResponse> cambiarEstado(
      @PathVariable Long id,
      @Valid @RequestBody EstadoUsuarioRequest request,
      Authentication authentication) {
    return service.cambiarEstado(id, request.activo(), authentication.getName())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/password")
  public ResponseEntity<UsuarioResponse> cambiarPassword(
      @PathVariable Long id,
      @Valid @RequestBody CambioPasswordRequest request,
      Authentication authentication) {
    return service.cambiarPassword(id, request.password(), authentication.getName())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(
      @PathVariable Long id, Authentication authentication) {
    return service.eliminar(id, authentication.getName())
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
