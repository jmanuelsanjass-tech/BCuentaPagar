package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.RecepcionMercanciaRequest;
import com.ejemplo.proveedor.dto.RecepcionMercanciaResponse;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.model.RecepcionMercancia;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import com.ejemplo.proveedor.repository.RecepcionMercanciaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecepcionMercanciaService {

  private final RecepcionMercanciaRepository repository;
  private final OrdenCompraRepository ordenCompraRepository;
  private final FacturaProveedorRepository facturaRepository;
  private final UuidV7Generator uuidV7Generator;

  public RecepcionMercanciaService(
      RecepcionMercanciaRepository repository,
      OrdenCompraRepository ordenCompraRepository,
      FacturaProveedorRepository facturaRepository,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.ordenCompraRepository = ordenCompraRepository;
    this.facturaRepository = facturaRepository;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<RecepcionMercanciaResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<RecepcionMercanciaResponse> obtener(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public RecepcionMercanciaResponse crear(RecepcionMercanciaRequest request) {
    OrdenCompra orden = validarRequest(request, null);
    RecepcionMercancia recepcion = new RecepcionMercancia();
    recepcion.setId(uuidV7Generator.generate());
    aplicarRequest(recepcion, orden, request);
    return toResponse(repository.saveAndFlush(recepcion));
  }

  @Transactional
  public Optional<RecepcionMercanciaResponse> actualizar(
      UUID id, RecepcionMercanciaRequest request) {
    return repository.findById(id).map(recepcion -> {
      if (facturaRepository.existsByRecepcion_Id(id)
          && (!recepcion.getEmpresaId().equals(request.empresaId())
              || !recepcion.getOrdenCompra().getIdOrdenCompra()
                  .equals(request.ordenCompraId()))) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede cambiar empresa u orden en una recepción facturada");
      }
      OrdenCompra orden = validarRequest(request, id);
      aplicarRequest(recepcion, orden, request);
      return toResponse(repository.saveAndFlush(recepcion));
    });
  }

  @Transactional
  public boolean eliminar(UUID id) {
    return repository.findById(id).map(recepcion -> {
      if (facturaRepository.existsByRecepcion_Id(id)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede eliminar una recepción vinculada a facturas");
      }
      repository.delete(recepcion);
      return true;
    }).orElse(false);
  }

  private OrdenCompra validarRequest(RecepcionMercanciaRequest request, UUID id) {
    boolean numeroDuplicado = id == null
        ? repository.existsByEmpresaIdAndNumeroRecepcion(
            request.empresaId(), request.numeroRecepcion().trim())
        : repository.existsByEmpresaIdAndNumeroRecepcionAndIdNot(
            request.empresaId(), request.numeroRecepcion().trim(), id);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de recepción para la empresa");
    }

    OrdenCompra orden = ordenCompraRepository.findById(request.ordenCompraId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la orden de compra indicada"));
    if (!orden.getEmpresaId().equals(request.empresaId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La orden de compra no pertenece a la empresa indicada");
    }
    return orden;
  }

  private void aplicarRequest(
      RecepcionMercancia recepcion, OrdenCompra orden, RecepcionMercanciaRequest request) {
    recepcion.setEmpresaId(request.empresaId());
    recepcion.setNumeroRecepcion(request.numeroRecepcion().trim());
    recepcion.setOrdenCompra(orden);
    recepcion.setFechaRecepcion(request.fechaRecepcion());
    recepcion.setRecibidoPor(request.recibidoPor());
  }

  private RecepcionMercanciaResponse toResponse(RecepcionMercancia recepcion) {
    return new RecepcionMercanciaResponse(
        recepcion.getId(),
        recepcion.getEmpresaId(),
        recepcion.getNumeroRecepcion(),
        recepcion.getOrdenCompra().getIdOrdenCompra(),
        recepcion.getFechaRecepcion(),
        recepcion.getRecibidoPor(),
        recepcion.getCreadoEn());
  }
}
