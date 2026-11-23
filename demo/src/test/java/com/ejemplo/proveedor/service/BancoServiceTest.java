package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.BancoRequest;
import com.ejemplo.proveedor.model.Banco;
import com.ejemplo.proveedor.repository.BancoRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class BancoServiceTest {

  @Test
  void crearAplicaValoresPorDefectoYNormalizaCodigos() {
    BancoRepository repository = mock(BancoRepository.class);
    when(repository.saveAndFlush(any(Banco.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    BancoService service = new BancoService(repository);

    var creado = service.crear(new BancoRequest(
        " bank-01 ", " Banco Ejemplo ", " Banco Ejemplo ", "abcdmxmm", null, null));

    assertEquals("bank-01", creado.codigoBanco());
    assertEquals("Banco Ejemplo", creado.nombre());
    assertEquals("Banco Ejemplo", creado.nombreCorto());
    assertEquals("ABCDMXMM", creado.codigoSwiftBic());
    assertEquals("MX", creado.pais());
    assertEquals(true, creado.activo());
  }

  @Test
  void crearRechazaCodigoBancoDuplicado() {
    BancoRepository repository = mock(BancoRepository.class);
    when(repository.existsByCodigoBanco("bank-01")).thenReturn(true);
    BancoService service = new BancoService(repository);

    assertThrows(ResponseStatusException.class, () -> service.crear(new BancoRequest(
        "bank-01", "Banco Ejemplo", null, null, null, true)));
  }

  @Test
  void actualizarActualizaCamposYFecha() {
    BancoRepository repository = mock(BancoRepository.class);
    UUID id = UUID.randomUUID();
    Banco banco = new Banco();
    banco.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(banco));
    when(repository.saveAndFlush(any(Banco.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    BancoService service = new BancoService(repository);

    var actualizado = service.actualizar(id, new BancoRequest(
        "bank-02", "Banco Actualizado", null, null, "us", false)).orElseThrow();

    assertEquals("US", actualizado.pais());
    assertEquals(false, actualizado.activo());
    assertEquals("Banco Actualizado", actualizado.nombre());
    verify(repository).saveAndFlush(banco);
  }
}
