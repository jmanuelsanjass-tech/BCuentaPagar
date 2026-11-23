ALTER TABLE public.ventas
    ADD COLUMN IF NOT EXISTS monto_recibido NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS monto_sobrante NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS tipo_moneda VARCHAR(20) NOT NULL DEFAULT 'Pesos',
    ADD COLUMN IF NOT EXISTS banco_corto VARCHAR(30);

UPDATE public.ventas
SET monto_recibido = total,
    monto_sobrante = 0.00
WHERE monto_recibido = 0.00
  AND total > 0.00;
