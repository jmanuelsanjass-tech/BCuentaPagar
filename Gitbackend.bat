@echo off
REM Script para automatizar flujo Git: pull, add, commit, push

REM Cambia al directorio del proyecto
cd /d D:\NET2026\cuentas-por-pagar\BCuentaPagar

REM Traer cambios remotos
echo === Actualizando rama main desde remoto ===
git pull origin main --no-edit

REM Agregar todos los cambios
echo === Agregando cambios ===
git add .

REM Crear commit con mensaje automático (puedes personalizarlo)
set /p msg="Escribe el mensaje del commit: "
git commit -m "%msg%"

REM Subir cambios al remoto
echo === Subiendo cambios a GitHub ===
git push origin main

echo === Proceso completado ===
pause
