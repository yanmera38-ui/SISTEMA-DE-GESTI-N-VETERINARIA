# VetCare - Clinica Veterinaria Huellitas

Sistema de escritorio en Java Swing para registrar clientes, mascotas, citas medicas e historial clinico basico.

## Ejecucion

### Abrir en Apache NetBeans

1. Abra Apache NetBeans.
2. Seleccione `File > Open Project`.
3. Elija la carpeta `SistemaDeGestiónVeterinaria`.
4. Presione `Open Project`.
5. Ejecute el proyecto con `Run Project` o la tecla `F6`.

Compilar:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

Ejecutar:

```powershell
java -cp out com.huellitas.vetcare.Main
```

Los datos se guardan automaticamente en la carpeta `data/` usando archivos CSV.

## Contenido

- `src/`: codigo fuente Java.
- `docs/`: portafolio de ingenieria, diagramas UML, plan QA, manual de usuario y justificacion arquitectonica.
