package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.RecepcionMercanciaLineaResponse;
import com.ejemplo.proveedor.service.RecepcionMercanciaLineaService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recepciones-mercancia/{recepcionId}/lineas")
public class RecepcionMercanciaLineaController {

  private final RecepcionMercanciaLineaService service;

  public RecepcionMercanciaLineaController(RecepcionMercanciaLineaService service) {
    this.service = service;
  }

  @GetMapping
  public List<RecepcionMercanciaLineaResponse> listar(@PathVariable UUID recepcionId) {
    return service.listar(recepcionId);
  }
}
