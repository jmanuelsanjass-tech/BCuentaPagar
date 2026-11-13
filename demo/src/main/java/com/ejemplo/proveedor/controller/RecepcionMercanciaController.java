package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.RecepcionMercanciaRequest;
import com.ejemplo.proveedor.dto.RecepcionMercanciaResponse;
import com.ejemplo.proveedor.service.RecepcionMercanciaService;
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
@RequestMapping("/api/recepciones-mercancia")
public class RecepcionMercanciaController {

  private final RecepcionMercanciaService service;

  public RecepcionMercanciaController(RecepcionMercanciaService service) {
    this.service = service;
  }

  @GetMapping
  public List<RecepcionMercanciaResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<RecepcionMercanciaResponse> obtener(@PathVariable UUID id) {
    return service.obtener(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<RecepcionMercanciaResponse> crear(
      @Valid @RequestBody RecepcionMercanciaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<RecepcionMercanciaResponse> actualizar(
      @PathVariable UUID id, @Valid @RequestBody RecepcionMercanciaRequest request) {
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
