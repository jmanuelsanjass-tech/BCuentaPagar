package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.PoliticaAprobacionRequest;
import com.ejemplo.proveedor.dto.PoliticaAprobacionResponse;
import com.ejemplo.proveedor.service.PoliticaAprobacionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/politicas-aprobacion")
public class PoliticaAprobacionController {

  private final PoliticaAprobacionService service;

  public PoliticaAprobacionController(PoliticaAprobacionService service) {
    this.service = service;
  }

  @GetMapping
  public List<PoliticaAprobacionResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PoliticaAprobacionResponse> obtener(@PathVariable UUID id) {
    return service.obtener(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<PoliticaAprobacionResponse> crear(
      @Valid @RequestBody PoliticaAprobacionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PoliticaAprobacionResponse> actualizar(
      @PathVariable UUID id, @Valid @RequestBody PoliticaAprobacionRequest request) {
    return service.actualizar(id, request).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
