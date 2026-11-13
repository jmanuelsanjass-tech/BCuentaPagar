package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.BitacoraAuditoriaRequest;
import com.ejemplo.proveedor.dto.BitacoraAuditoriaResponse;
import com.ejemplo.proveedor.service.BitacoraAuditoriaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bitacora-auditoria")
public class BitacoraAuditoriaController {

  private final BitacoraAuditoriaService service;

  public BitacoraAuditoriaController(BitacoraAuditoriaService service) {
    this.service = service;
  }

  @GetMapping
  public List<BitacoraAuditoriaResponse> listar(
      @RequestParam(required = false) Integer limit) {
    if (limit == null) {
      return service.listar();
    }
    if (limit < 1 || limit > 500) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El límite debe estar entre 1 y 500");
    }
    return service.listarRecientes(PageRequest.of(
        0, limit, Sort.by(Sort.Direction.DESC, "cambiadoEn")));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BitacoraAuditoriaResponse> obtener(@PathVariable Long id) {
    return service.obtener(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<BitacoraAuditoriaResponse> crear(
      @Valid @RequestBody BitacoraAuditoriaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BitacoraAuditoriaResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody BitacoraAuditoriaRequest request) {
    return service.actualizar(id, request).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
