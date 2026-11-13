package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.RecepcionMercanciaLineaResponse;
import com.ejemplo.proveedor.repository.RecepcionMercanciaLineaRepository;
import com.ejemplo.proveedor.repository.RecepcionMercanciaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecepcionMercanciaLineaService {

  private final RecepcionMercanciaLineaRepository repository;
  private final RecepcionMercanciaRepository recepcionRepository;

  public RecepcionMercanciaLineaService(
      RecepcionMercanciaLineaRepository repository,
      RecepcionMercanciaRepository recepcionRepository) {
    this.repository = repository;
    this.recepcionRepository = recepcionRepository;
  }

  @Transactional(readOnly = true)
  public List<RecepcionMercanciaLineaResponse> listar(UUID recepcionId) {
    if (!recepcionRepository.existsById(recepcionId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "No existe la recepción indicada");
    }

    return repository.findAllByRecepcion_IdOrderByIdAsc(recepcionId).stream()
        .map(linea -> new RecepcionMercanciaLineaResponse(
            linea.getId(),
            linea.getRecepcion().getId(),
            linea.getOrdenCompraLineaId(),
            linea.getCantidadRecibida(),
            linea.getNotas()))
        .toList();
  }
}
