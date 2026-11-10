package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "cuentas_por_cobrar", schema = "public")
@Check(name = "chk_estatus_cuentas_por_cobrar", constraints = "estatus IN ('Pendiente','Pagado','Cancelado')")
@Check(name = "chk_importe_original", constraints = "importe_original >= 0")
@Check(name = "chk_saldo_pendiente", constraints = "saldo_pendiente >= 0")
@Check(name = "chk_dias_vencidos", constraints = "dias_vencidos >= 0")
@Getter
@Setter
public class CuentaPorCobrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cxc", nullable = false)
    private Long idCxc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false, foreignKey = @ForeignKey(name = "fk_client"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false, foreignKey = @ForeignKey(name = "fk_venta"))
    private Venta venta;

    @NotNull(message = "La fecha de venta es obligatoria")
    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 16, fraction = 2)
    @Column(name = "importe_original", nullable = false, precision = 18, scale = 2)
    private BigDecimal importeOriginal = BigDecimal.ZERO.setScale(2);

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 16, fraction = 2)
    @Column(name = "saldo_pendiente", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoPendiente = BigDecimal.ZERO.setScale(2);

    @NotNull
    @Min(0)
    @Column(name = "dias_vencidos", nullable = false)
    private Integer diasVencidos = 0;

    @DecimalMin("0.00")
    @Digits(integer = 16, fraction = 2)
    @Column(name = "limite_credito", precision = 18, scale = 2, columnDefinition = "numeric(18,2) default 0")
    private BigDecimal limiteCredito = BigDecimal.ZERO.setScale(2);

    @NotNull
    @Pattern(regexp = "Pendiente|Pagado|Cancelado", message = "El estatus debe ser Pendiente, Pagado o Cancelado")
    @Size(max = 20)
    @Column(name = "estatus", nullable = false, length = 20, columnDefinition = "varchar(20) default 'Pendiente'")
    private String estatus = "Pendiente";

    @Column(name = "fecha_ultimo_pago")
    private LocalDate fechaUltimoPago;
}
