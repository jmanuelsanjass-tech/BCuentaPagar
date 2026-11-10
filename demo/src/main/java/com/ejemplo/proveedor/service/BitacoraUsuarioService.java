package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.BitacoraUsuarioResponse;
import com.ejemplo.proveedor.model.BitacoraUsuario;
import com.ejemplo.proveedor.repository.BitacoraUsuarioRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BitacoraUsuarioService {

  private final BitacoraUsuarioRepository repository;

  public BitacoraUsuarioService(BitacoraUsuarioRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public void registrar(
      String administrador,
      Long idUsuarioObjetivo,
      String usernameObjetivo,
      String accion,
      String detalle) {
    BitacoraUsuario evento = new BitacoraUsuario();
    evento.setAdministrador(administrador);
    evento.setIdUsuarioObjetivo(idUsuarioObjetivo);
    evento.setUsernameObjetivo(usernameObjetivo);
    evento.setAccion(accion);
    evento.setDetalle(detalle);
    repository.save(evento);
  }

  @Transactional(readOnly = true)
  public List<BitacoraUsuarioResponse> listar(int limite) {
    int limiteSeguro = Math.max(1, Math.min(limite, 500));
    return repository.findAllByOrderByFechaEventoDesc(PageRequest.of(0, limiteSeguro)).stream()
        .map(evento -> new BitacoraUsuarioResponse(
            evento.getIdEvento(),
            evento.getAdministrador(),
            evento.getIdUsuarioObjetivo(),
            evento.getUsernameObjetivo(),
            evento.getAccion(),
            evento.getDetalle(),
            evento.getFechaEvento()))
        .toList();
  }
}
