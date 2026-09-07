# PDF a PNG Movil

App movil nativa en **Kotlin** para Android (12+) que convierte un PDF en imagenes PNG de alta calidad y te permite compartirlas directamente por **WhatsApp** o **WhatsApp Business**, en un solo paso.

## Caracteristicas

- Seleccion de cualquier PDF descargado en tu carpeta de Descargas (o desde tu almacenamiento) sin permisos especiales.
- Conversion automatica de **todas las paginas** del PDF (incluso si es una sola pagina) a PNG de alta calidad y peso liviano.
- Boton **"Convertir y enviar por WhatsApp"**: convierte y abre WhatsApp con todas las imagenes listas para adjuntar.
- Limpieza automatica: despues de confirmar el envio, las imagenes temporales se eliminan y tu **PDF original se conserva**.
- Calidad de imagen siempre en **Alta** (de forma transparente para el usuario).
- Pagina "Acerca de esta App" con la informacion del autor y sus medios de contacto.

## Requisitos

- Android 12 o superior (minSdk 31, targetSdk 35).

## Instalacion (APK)

1. Descarga `mpp.apk` (se genera en la carpeta `bin/` del proyecto).
2. En el telefono, abre el archivo y permite la instalacion por fuentes desconocidas cuando se solicite.
3. Tambien puedes instalarlo por ADB:

   ```
   adb install -r bin/mpp.apk
   ```

## Como usar

1. Toca **"Seleccionar PDF"** y elige un PDF descargado.
2. Toca el boton verde **"Convertir y enviar por WhatsApp"**.
3. En WhatsApp se adjuntan todas las paginas como imagenes PNG; elige el chat y envia.
4. Al volver a la app, confirma el envio y las imagenes temporales se eliminan automaticamente, conservando el PDF original.

## Compilar desde el codigo fuente

Requisitos: JDK 17+, Android SDK (compileSdk 35) y Gradle 8.13.

```
gradle :app:assembleRelease
```

El APK firmado queda en `app/build/outputs/apk/release/`. Si no se dispone del keystore local, el APK se genera sin firmar.

## Detalles tecnicos

- Lenguaje: **Kotlin** con Jetpack Compose (100% nativo, sin dependencias de terceros para el render).
- Renderizado de PDF: `PdfRenderer` integrado en Android.
- Compartir: `ACTION_SEND_MULTIPLE` + `FileProvider`, compatible con WhatsApp y WhatsApp Business.
- Minificacion con R8 habilitada (APK liviano, ~2 MB).

## Acerca del autor

Hecho por **Luis Gonzalez (Lantonium)** - AI Engineer | Programmer | Entrepreneur.

- Sitio: https://lantonium.com/acerca
- GitHub: https://github.com/lgonzalh
- LinkedIn: https://www.linkedin.com/in/luis-gonzalez
- WhatsApp: https://wa.me/573246864991
- Email: lgonzalh@outlook.com

## Licencia

Este proyecto esta bajo la licencia **Creative Commons Attribution 4.0 Internacional (CC BY 4.0)**.
Puedes usar, copiar, modificar y distribuir el proyecto libremente, siempre que se atribuya el credito al autor (Luis Gonzalez / Lantonium). Consulta el archivo `LICENSE` para los terminos completos.