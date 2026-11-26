# Hola Mundo · Kotlin + Android

[![CI](https://github.com/VicentePerezHdz/holamundo-kotlin-android/actions/workflows/ci.yml/badge.svg)](https://github.com/VicentePerezHdz/holamundo-kotlin-android/actions/workflows/ci.yml) [![Licencia: MIT](https://img.shields.io/badge/licencia-MIT-blue.svg)](LICENSE)

App Android multimódulo con MVVM y Dagger 2. Funciona sin servidor (offline-first) y tiene tres funciones: saludo con input de texto, calculadora y combobox con mensaje.

## Qué demuestra
- Módulos `:core` (dominio, datos y casos de uso), `:desing` (componentes visuales) y `:app` (UI).
- MVVM con `ViewModel`, casos de uso y repositorios detrás de interfaces.
- Inyección de dependencias con Dagger 2.
- Kotlin y Java conviviendo en el mismo módulo.
- Variantes `debug`, `quality` y `release` con distinto `applicationId`.

## Requisitos
JDK 17, Android SDK y Android Studio. Al abrir el proyecto, Android Studio crea `local.properties` con la ruta del SDK.

## Cómo correrlo
```powershell
.\gradlew :core:testDebugUnitTest   # 4 pruebas unitarias, sin emulador
.\gradlew :app:assembleDebug        # genera el APK de debug
.\gradlew :app:installDebug         # lo instala en el emulador o teléfono
```

La explicación completa está en [`docs/Documento_Transicion.md`](docs/Documento_Transicion.md).

## Licencia

[MIT](LICENSE)
