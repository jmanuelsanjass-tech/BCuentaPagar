package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.CuentaPorCobrarHistoriaRequest;
import com.ejemplo.proveedor.dto.CuentaPorCobrarHistoriaResponse;
import com.ejemplo.proveedor.model.CuentaPorCobrar;
import com.ejemplo.proveedor.model.CuentaPorCobrarHistoria;
import com.ejemplo.proveedor.repository.CuentaPorCobrarHistoriaRepository;
import com.ejemplo.proveedor.repository.CuentaPorCobrarRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaPorCobrarHistoriaService {

  private final CuentaPorCobrarHistoriaRepository repository;
  private final CuentaPorCobrarRepository cuentaRepository;

  @PersistenceContext
  private EntityManager entityManager;

  public CuentaPorCobrarHistoriaService(
      CuentaPorCobrarHistoriaRepository repository,
      CuentaPorCobrarRepository cuentaRepository) {
    this.repository = repository;
    this.cuentaRepository = cuentaRepository;
  }

  @Transactional(readOnly = true)
  public List<CuentaPorCobrarHistoriaResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<CuentaPorCobrarHistoriaResponse> obtener(Long idHistoria) {
    return repository.findById(idHistoria).map(this::toResponse);
  }

  @Transactional
  public CuentaPorCobrarHistoriaResponse crear(CuentaPorCobrarHistoriaRequest request) {
    if (request.idHistoria() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "idHistoria es obligatorio porque la tabla no genera el ID");
    }
    if (repository.existsById(request.idHistoria())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe un movimiento con ese idHistoria");
    }

    CuentaPorCobrarHistoria historia = new CuentaPorCobrarHistoria();
    historia.setIdHistoria(request.idHistoria());
    aplicarRequest(historia, request);
    CuentaPorCobrarHistoria guardada = repository.saveAndFlush(historia);
    entityManager.refresh(guardada);
    return toResponse(guardada);
  }

  @Transactional
  public Optional<CuentaPorCobrarHistoriaResponse> actualizar(
      Long idHistoria, CuentaPorCobrarHistoriaRequest request) {
    return repository.findById(idHistoria).map(historia -> {
      aplicarRequest(historia, request);
      repository.saveAndFlush(historia);
      entityManager.refresh(historia);
      return toResponse(historia);
    });
  }

  @Transactional
  public boolean eliminar(Long idHistoria) {
    return repository.findById(idHistoria).map(historia -> {
      repository.delete(historia);
      return true;
    }).orElse(false);
  }

  private void aplicarRequest(
      CuentaPorCobrarHistoria historia, CuentaPorCobrarHistoriaRequest request) {
    CuentaPorCobrar cuenta = cuentaRepository.findById(request.cuentaPorCobrarId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la cuenta por cobrar indicada"));

    historia.setCuentaPorCobrar(cuenta);
    historia.setTipoMovimiento(request.tipoMovimiento());
    historia.setImporteOriginal(request.importeOriginal());
    historia.setSaldoAnterior(request.saldoAnterior());
    historia.setSaldoNuevo(request.saldoNuevo());
    historia.setMontoMovimiento(request.montoMovimiento());
    historia.setFechaVencimientoAnterior(request.fechaVencimientoAnterior());
    historia.setFechaVencimientoNueva(request.fechaVencimientoNueva());
    historia.setDiasVencidos(request.diasVencidos());
    historia.setUsuarioMovimiento(request.usuarioMovimiento());
    historia.setEquipoMovimiento(request.equipoMovimiento());
    historia.setObservaciones(request.observaciones());
  }

  private CuentaPorCobrarHistoriaResponse toResponse(CuentaPorCobrarHistoria historia) {
    return new CuentaPorCobrarHistoriaResponse(
        historia.getIdHistoria(),
        historia.getCuentaPorCobrar().getIdCxc(),
        historia.getFechaMovimiento(),
        historia.getTipoMovimiento(),
        historia.getImporteOriginal(),
        historia.getSaldoAnterior(),
        historia.getSaldoNuevo(),
        historia.getMontoMovimiento(),
        historia.getFechaVencimientoAnterior(),
        historia.getFechaVencimientoNueva(),
        historia.getDiasVencidos(),
        historia.getUsuarioMovimiento(),
        historia.getEquipoMovimiento(),
        historia.getObservaciones(),
        historia.getFechaLog());
  }
}
