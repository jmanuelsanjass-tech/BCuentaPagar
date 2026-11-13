INSERT INTO public.roles (nombre, descripcion)
VALUES
    ('COMPRAS', 'Consulta y administra órdenes de compra'),
    ('RECEPCION', 'Consulta recepciones de mercancía'),
    ('TESORERIA', 'Consulta pagos a proveedores'),
    ('AUDITOR', 'Consulta la bitácora de auditoría')
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion;
