package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.BitacoraAuditoriaRequest;
import com.ejemplo.proveedor.dto.BitacoraAuditoriaResponse;
import com.ejemplo.proveedor.model.BitacoraAuditoria;
import com.ejemplo.proveedor.repository.BitacoraAuditoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BitacoraAuditoriaService {

  private final BitacoraAuditoriaRepository repository;

  public BitacoraAuditoriaService(BitacoraAuditoriaRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<BitacoraAuditoriaResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public List<BitacoraAuditoriaResponse> listarRecientes(Pageable pageable) {
    return repository.findAll(pageable).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<BitacoraAuditoriaResponse> obtener(Long id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public BitacoraAuditoriaResponse crear(BitacoraAuditoriaRequest request) {
    BitacoraAuditoria registro = new BitacoraAuditoria();
    aplicarRequest(registro, request);
    return toResponse(repository.saveAndFlush(registro));
  }

  @Transactional
  public Optional<BitacoraAuditoriaResponse> actualizar(
      Long id, BitacoraAuditoriaRequest request) {
    return repository.findById(id).map(registro -> {
      aplicarRequest(registro, request);
      return toResponse(repository.saveAndFlush(registro));
    });
  }

  @Transactional
  public boolean eliminar(Long id) {
    return repository.findById(id).map(registro -> {
      repository.delete(registro);
      return true;
    }).orElse(false);
  }

  private void aplicarRequest(
      BitacoraAuditoria registro, BitacoraAuditoriaRequest request) {
    registro.setTabla(request.tabla().trim());
    registro.setOperacion(request.operacion());
    registro.setRegistroId(request.registroId());
    registro.setDatosAnteriores(request.datosAnteriores());
    registro.setDatosNuevos(request.datosNuevos());
  }

  private BitacoraAuditoriaResponse toResponse(BitacoraAuditoria registro) {
    return new BitacoraAuditoriaResponse(
        registro.getId(),
        registro.getTabla(),
        registro.getOperacion(),
        registro.getRegistroId(),
        registro.getDatosAnteriores(),
        registro.getDatosNuevos(),
        registro.getCambiadoEn(),
        registro.getCambiadoPor(),
        registro.getIdTransaccion());
  }
}
