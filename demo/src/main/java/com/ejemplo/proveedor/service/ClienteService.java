package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.ClienteRequest;
import com.ejemplo.proveedor.dto.ClienteResponse;
import com.ejemplo.proveedor.model.Cliente;
import com.ejemplo.proveedor.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

  private final ClienteRepository repository;

  @PersistenceContext
  private EntityManager entityManager;

  public ClienteService(ClienteRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<ClienteResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<ClienteResponse> obtener(Long id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public ClienteResponse crear(ClienteRequest request) {
    Cliente cliente = new Cliente();
    aplicar(cliente, request);
    Cliente guardado = repository.saveAndFlush(cliente);
    entityManager.refresh(guardado);
    return toResponse(guardado);
  }

  @Transactional
  public Optional<ClienteResponse> actualizar(Long id, ClienteRequest request) {
    return repository.findById(id).map(cliente -> {
      aplicar(cliente, request);
      repository.saveAndFlush(cliente);
      return cargar(id);
    });
  }

  @Transactional
  public boolean eliminar(Long id) {
    return repository.findById(id).map(cliente -> {
      repository.delete(cliente);
      return true;
    }).orElse(false);
  }

  private void aplicar(Cliente cliente, ClienteRequest request) {
    cliente.setNombreComercial(request.nombreComercial());
    cliente.setRazonSocial(request.razonSocial());
    cliente.setRfc(request.rfc());
    cliente.setSituacionFiscal(request.situacionFiscal());
    cliente.setTipoCliente(request.tipoCliente());
    cliente.setDireccion(request.direccion());
    cliente.setColonia(request.colonia());
    cliente.setCiudad(request.ciudad());
    cliente.setContacto(request.contacto());
    cliente.setBanco(request.banco());
    cliente.setCuentaBancaria(request.cuentaBancaria());
    cliente.setClabe(request.clabe());
    cliente.setFormaPago(
        request.formaPago() == null ? "Transferencia" : request.formaPago());
  }

  private ClienteResponse cargar(Long id) {
    return repository.findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> new IllegalStateException("No se pudo recargar el cliente guardado"));
  }

  private ClienteResponse toResponse(Cliente cliente) {
    return new ClienteResponse(
        cliente.getId(),
        cliente.getNombreComercial(),
        cliente.getRazonSocial(),
        cliente.getRfc(),
        cliente.getSituacionFiscal(),
        cliente.getTipoCliente(),
        cliente.getDireccion(),
        cliente.getColonia(),
        cliente.getCiudad(),
        cliente.getContacto(),
        cliente.getBanco(),
        cliente.getCuentaBancaria(),
        cliente.getClabe(),
        cliente.getFormaPago(),
        cliente.getFechaAlta());
  }
}
