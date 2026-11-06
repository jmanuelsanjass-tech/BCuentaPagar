package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.CuentaPorCobrarRequest;
import com.ejemplo.proveedor.dto.CuentaPorCobrarResponse;
import com.ejemplo.proveedor.model.Cliente;
import com.ejemplo.proveedor.model.CuentaPorCobrar;
import com.ejemplo.proveedor.model.Venta;
import com.ejemplo.proveedor.repository.ClienteRepository;
import com.ejemplo.proveedor.repository.CuentaPorCobrarRepository;
import com.ejemplo.proveedor.repository.VentaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CuentaPorCobrarServiceTest {

  @Test
  void crearAplicaValoresPorDefectoYEnlazaClienteVenta() {
    CuentaPorCobrarRepository repository = mock(CuentaPorCobrarRepository.class);
    ClienteRepository clienteRepository = mock(ClienteRepository.class);
    VentaRepository ventaRepository = mock(VentaRepository.class);
    Cliente cliente = new Cliente();
    cliente.setIdCliente(3L);
    Venta venta = new Venta();
    venta.setIdVenta(8L);
    venta.setCliente(cliente);

    when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));
    when(ventaRepository.findById(8L)).thenReturn(Optional.of(venta));
    when(repository.saveAndFlush(any(CuentaPorCobrar.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CuentaPorCobrarService service =
        new CuentaPorCobrarService(repository, clienteRepository, ventaRepository);
    CuentaPorCobrarRequest request = new CuentaPorCobrarRequest(
        3L, 8L, LocalDate.parse("2026-11-05"), LocalDate.parse("2026-12-05"),
        null, null, null, null, null, null);

    CuentaPorCobrarResponse creada = service.crear(request);

    assertEquals(3L, creada.clienteId());
    assertEquals(8L, creada.ventaId());
    assertEquals("0.00", creada.importeOriginal().toPlainString());
    assertEquals("0.00", creada.saldoPendiente().toPlainString());
    assertEquals(0, creada.diasVencidos());
    assertEquals("0.00", creada.limiteCredito().toPlainString());
    assertEquals("Pendiente", creada.estatus());
  }
}
