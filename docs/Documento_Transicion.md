# Documento de transición: Hola Mundo Kotlin

> Manual para entender qué se construyó, qué hace cada clase, método e interfaz, y **cómo volver a hacer el proyecto desde cero**.
> Fecha: 2026-11-23 · Ruta: `C:\HolaMundo\02-Kotlin-Android`

## 1. Qué hace la aplicación

Una sola pantalla Android con tres tarjetas, igual que el proyecto Angular, pero **sin servidor**: todo vive en el teléfono (offline-first).

| Tarjeta | Control en pantalla | Quién resuelve | Resultado |
|---|---|---|---|
| Saludo | `EditText` + botón | `SaludarUseCase` → `SaludoController` (Java) | "¡Hola, Vicente!…" o error si el nombre tiene menos de 2 letras |
| Calculadora | 2 `EditText` + `Spinner` de operación + botón | `CalcularUseCase` (BigDecimal) | `10 + 5 = 15`, o error al dividir entre 0 |
| Combobox | `Spinner` llenado desde el repositorio | `ObtenerLenguajesUseCase` → `CatalogoRepository` → `CatalogoLocalDataSource` | Mensaje de la opción elegida |

## 2. Cómo correrlo

Requisitos que ya tienes: JDK 17, Android SDK (`%LOCALAPPDATA%\Android\Sdk`), Android Studio.

```powershell
cd C:\HolaMundo\02-Kotlin-Android
.\gradlew :core:testDebugUnitTest     # 4 pruebas unitarias en la JVM, sin emulador
.\gradlew :app:assembleDebug          # genera app\build\outputs\apk\debug\app-debug.apk
.\gradlew :app:installDebug           # lo instala en el emulador o teléfono conectado
```

O en Android Studio: **File → Open → `C:\HolaMundo\02-Kotlin-Android`**, espera el *Gradle Sync* y presiona ▶ con la configuración `app`.

Variantes: `debug` (id `com.holamundo.app.dev`), `quality` (`.qa`) y `release`. Se pueden instalar las tres a la vez porque tienen distinto `applicationId`.

## 3. Arquitectura

```
┌──────────────────────────── módulo :app (shell) ────────────────────────────┐
│ App (Application) ── initApplication() ─► InjectorCore.init()               │
│                                          Injector.init(InjectorCore.component)│
│ MainActivity ── ViewBinding ── observa LiveData ──┐                          │
│      │ @Inject ViewModelFactory                   │                          │
│      ▼                                            │                          │
│ MainViewModel (saludo, calculo, lenguajes, mensajeLenguaje)                  │
└──────┼───────────────────────────────────────────────────────────────────────┘
       │ llama casos de uso (operator fun invoke)
┌──────▼──────────────────────── módulo :core ────────────────────────────────┐
│ domain/usecase   SaludarUseCase  CalcularUseCase  ObtenerLenguajesUseCase    │
│                       │                                  │                   │
│ controller       SaludoController.java          domain/repository            │
│                                                 CatalogoRepository (interfaz)│
│                                                          ▲ @Binds            │
│ data/repository                              CatalogoRepositoryImpl (IO)     │
│ data/local                                   CatalogoLocalDataSource         │
│ di               CoreModule, CoreComponent, InjectorCore                     │
└──────────────────────────────────────────────────────────────────────────────┘
┌──────────────────────────── módulo :desing ─────────────────────────────────┐
│ Colores, estilos (Desing.Tarjeta, Desing.Titulo), TextView.mostrarExito/Error │
└──────────────────────────────────────────────────────────────────────────────┘
Dependencias: :app → :core, :desing      (:core y :desing no se conocen)
```

Ideas clave:

- **Multimódulo**: `:core` (negocio), `:desing` (sistema de diseño) y `:app` (shell).
- **Clean Architecture dentro de `:core`**: `domain` (modelos, interfaces, casos de uso) no depende de `data`; `data` implementa las interfaces de `domain`.
- **MVVM en la UI**: la Activity solo pinta; el ViewModel guarda el estado en `LiveData` y sobrevive a la rotación.
- **Dagger 2 con un grafo por módulo** (`InjectorCore`, `Injector`), conectados con `@Component(dependencies = …)`.
- **Java y Kotlin conviven**: `SaludoController.java` imita la lógica heredada en Controllers Java.
- **Offline-first**: el combo se lee de una fuente local.

## 4. Recorrido de un evento (Calcular)

1. El usuario escribe 10 y 5, elige `÷` y toca **Calcular**.
2. `MainActivity.configurarCalculadora()` tenía registrado el `setOnClickListener`; lee los textos con `binding.etNumeroA` y la posición del `Spinner`, y llama `viewModel.calcular("10", "5", Operacion.DIVISION)`.
3. `MainViewModel.calcular()` invoca `calcularUseCase(a, b, operacion)` (gracias a `operator fun invoke` se llama como función).
4. `CalcularUseCase` convierte con `toBigDecimalOrNull()`; si falla devuelve `Resultado.Error`. Si `b == 0` devuelve `Resultado.Error("No se puede dividir entre cero.")`; si no, `Resultado.Exito("10 ÷ 5 = 2")`.
5. El ViewModel asigna `_calculo.value = …`.
6. La Activity estaba observando `viewModel.calculo`; su lambda llama `pintar()`, que con `when` decide `mostrarExito` (verde) o `mostrarError` (rojo), funciones de extensión de `:desing`.

El combo es igual pero **asíncrono**: en el `init` del ViewModel, `viewModelScope.launch { … }` llama al caso de uso `suspend`, el repositorio cambia a `Dispatchers.IO` con `withContext`, y al volver la lista se publica en `_lenguajes`.

## 5. Inventario de clases, interfaces y métodos

### Módulo `:core`

| Archivo | Tipo | Miembros | Responsabilidad |
|---|---|---|---|
| `domain/model/Modelos.kt` | `enum`, `data class`, `sealed class` | `Operacion(simbolo)`, `OpcionCatalogo(clave, texto, mensaje)`, `Resultado.Exito / Resultado.Error` | Modelos del dominio; `Resultado` equivale a 200 vs ProblemDetails en .NET |
| `domain/repository/CatalogoRepository.kt` | interfaz | `suspend obtenerLenguajes()` | Contrato que el dominio conoce; no sabe si es BD o API |
| `domain/usecase/CasosDeUso.kt` | clases | `SaludarUseCase()`, `CalcularUseCase()`, `ObtenerLenguajesUseCase()` (todas `operator fun invoke`) | Una clase por acción de negocio |
| `controller/SaludoController.java` | clase Java | `validarNombre()`, `armarSaludo()`, `MIN_CARACTERES` | Lógica "heredada" en Java |
| `data/local/CatalogoLocalDataSource.kt` | clase | `suspend leerLenguajes()` | Simula la tabla local |
| `data/repository/CatalogoRepositoryImpl.kt` | clase | `obtenerLenguajes()` | Implementa la interfaz; lee en `Dispatchers.IO` |
| `di/CoreDi.kt` | `@Module`, `@Component`, `object` | `CoreModule.bindCatalogoRepository`, `CoreComponent` (expone los 3 casos de uso), `InjectorCore.init()` | Grafo Dagger de `:core` |
| `test/.../CasosDeUsoTest.kt` | JUnit 4 | 4 pruebas | Suma, división entre 0, nombre vacío, repositorio falso |

### Módulo `:app`

| Archivo | Tipo | Miembros | Responsabilidad |
|---|---|---|---|
| `App.kt` | `Application` | `onCreate()`, `initApplication()` | Arranca los injectors en orden de dependencia |
| `di/AppDi.kt` | `@Scope`, `@Component`, fábrica, `object` | `AppScope`, `AppComponent.inject(MainActivity)`, `ViewModelFactory.create()`, `Injector.init(core)` | Grafo Dagger de `:app` que depende de `CoreComponent` |
| `ui/main/MainViewModel.kt` | `ViewModel` | `saludar()`, `calcular()`, `seleccionarLenguaje()`; LiveData `saludo`, `calculo`, `lenguajes`, `mensajeLenguaje` | Estado de la pantalla |
| `ui/main/MainActivity.kt` | `AppCompatActivity` | `onCreate()`, `configurarSaludo()`, `configurarCalculadora()`, `configurarCombo()`, `pintar()` | Pinta y reenvía eventos |
| `res/layout/activity_main.xml` | layout | ids `etNombre`, `btnSaludar`, `tvSaludo`, `etNumeroA`, `spOperacion`, `etNumeroB`, `btnCalcular`, `tvResultado`, `spLenguaje`, `tvMensajeLenguaje` | De aquí se genera `ActivityMainBinding` |
| `res/values/strings.xml` | recursos | textos | Regla: nada de textos fijos en código |
| `build.gradle.kts` | Gradle | `buildTypes` debug/quality/release, `viewBinding` | Configuración del shell |

### Módulo `:desing`

| Archivo | Tipo | Miembros | Responsabilidad |
|---|---|---|---|
| `MensajeView.kt` | funciones de extensión | `TextView.mostrarExito()`, `TextView.mostrarError()` | Mismo look en toda la app |
| `res/values/desing.xml` | recursos | `desing_primario/exito/error`, `Desing.Tarjeta`, `Desing.Titulo` | Colores y estilos compartidos |

### Raíz

| Archivo | Para qué |
|---|---|
| `settings.gradle.kts` | Declara los módulos `:core`, `:desing`, `:app` y los repositorios |
| `build.gradle.kts` | Declara plugins con `apply false` |
| `gradle/libs.versions.toml` | Catálogo de versiones (AGP 8.7.3, Kotlin 2.1.10, Dagger 2.55, Lifecycle 2.8.7) |
| `gradle.properties` | Memoria de Gradle, AndroidX, `nonTransitiveRClass` |
| `local.properties` | Ruta del Android SDK (no se sube a git) |

## 6. Conceptos clave

| Concepto | Dónde verlo | En una frase |
|---|---|---|
| MVVM | `MainActivity` + `MainViewModel` | La vista observa; el ViewModel tiene el estado y no conoce la vista |
| LiveData | `MainViewModel` | Observable que respeta el ciclo de vida: no pinta si la Activity está detenida |
| `_privada` / pública | `MainViewModel` | Solo el ViewModel escribe; la vista solo lee |
| Coroutines | `viewModelScope.launch`, `suspend`, `withContext(Dispatchers.IO)` | Código asíncrono que se lee secuencial; IO fuera del hilo principal |
| Dagger 2 | `CoreDi.kt`, `AppDi.kt` | DI en tiempo de compilación; genera `DaggerCoreComponent` con kapt |
| `@Binds` vs `@Provides` | `CoreModule` | `@Binds` liga interfaz→implementación sin escribir código |
| Scopes | `@Singleton`, `@AppScope` | Dos componentes dependientes no pueden compartir scope |
| ViewModelFactory | `AppDi.kt` | Android crea los ViewModel; la fábrica le pasa las dependencias |
| Clean Architecture | paquetes `domain` / `data` | El dominio define interfaces; data las implementa |
| Sealed class + `when` | `Resultado`, `pintar()` | El compilador obliga a cubrir todos los casos |
| Extension functions | `:desing` | Agregar métodos a una clase sin heredar |
| Interop Java↔Kotlin | `SaludoController.java` | Kotlin llama Java como si fuera Kotlin; `String` nullable de Java llega como `String?` |
| ViewBinding | `ActivityMainBinding` | Acceso tipado a las vistas, sin `findViewById` ni ButterKnife |
| Pruebas unitarias | `CasosDeUsoTest` | Casos de uso sin Android, con un repositorio falso |

## 7. Paso a paso para volver a hacer el proyecto desde cero

Haz cada paso y compila antes de seguir. Así, si algo falla, sabes qué paso fue.

### Paso 1. Crear el proyecto
1. Android Studio → **New Project → Empty Views Activity** (la de Views, no Compose).
2. Nombre `HolaMundo`, paquete `com.holamundo.app`, lenguaje **Kotlin**, mínimo **API 24**, *Build configuration language*: **Kotlin DSL**.
3. Ruta: `C:\HolaMundo\02-Kotlin-Android`. Finish y espera el Sync.

### Paso 2. Catálogo de versiones
Reemplaza `gradle/libs.versions.toml` con el de este proyecto. Lo importante: Dagger (`dagger`, `dagger-compiler`), Lifecycle (`viewmodel-ktx`, `livedata-ktx`), `activity-ktx`, coroutines y el plugin `kotlin-kapt`. En el `build.gradle.kts` raíz declara los 4 plugins con `apply false`.

### Paso 3. Crear los módulos `:core` y `:desing`
1. **File → New → New Module → Android Library**. Nombre `core`, paquete `com.holamundo.core`. Repite con `desing` (`com.holamundo.desing`).
2. Revisa que `settings.gradle.kts` tenga `include(":core")`, `include(":desing")`, `include(":app")`.
3. En `core/build.gradle.kts`: plugins `android.library`, `kotlin.android`, `kotlin.kapt`; dependencias `api(libs.dagger)`, `api(libs.coroutines.android)`, `kapt(libs.dagger.compiler)`, `testImplementation(libs.junit)`.
4. En `desing/build.gradle.kts`: `api(libs.material)` y `core-ktx`.
5. En `app/build.gradle.kts`: `implementation(project(":core"))`, `implementation(project(":desing"))`, Lifecycle, activity-ktx, `kapt(libs.dagger.compiler)` y `buildFeatures { viewBinding = true }`.
6. **Sync**. ✅ Compila vacío.

### Paso 4. Dominio en `:core`
Crea los paquetes `domain/model`, `domain/repository`, `domain/usecase`, `data/local`, `data/repository`, `controller`, `di`.
1. `Modelos.kt`: `Operacion`, `OpcionCatalogo`, `Resultado`.
2. `CatalogoRepository.kt`: la interfaz con `suspend fun obtenerLenguajes()`.
3. `SaludoController.java` con `@Inject public SaludoController() {}`.
4. `CasosDeUso.kt`: los tres casos de uso con `@Inject constructor` y `operator fun invoke`.

### Paso 5. Datos en `:core`
1. `CatalogoLocalDataSource` con `@Inject constructor()` que devuelve la lista.
2. `CatalogoRepositoryImpl : CatalogoRepository` que usa `withContext(Dispatchers.IO)`.

### Paso 6. Dagger en `:core`
1. `CoreModule` abstracto con `@Binds @Singleton bindCatalogoRepository(impl)`.
2. `CoreComponent` con `@Singleton @Component(modules = [CoreModule::class])` y un método por caso de uso que quieras exponer.
3. `object InjectorCore` con `init()` → `DaggerCoreComponent.create()`.
4. **Build → Make Project**. Si `DaggerCoreComponent` sale en rojo, compila una vez: kapt lo genera.

### Paso 7. Pruebas de `:core`
Copia `CasosDeUsoTest.kt` en `core/src/test/java/com/holamundo/core/` y corre `.\gradlew :core:testDebugUnitTest`. ✅ 4 pruebas en verde.

### Paso 8. Diseño en `:desing`
1. `res/values/desing.xml` con colores y estilos `Desing.Tarjeta` y `Desing.Titulo`.
2. `MensajeView.kt` con `mostrarExito` y `mostrarError`.

### Paso 9. DI de `:app`
1. `AppDi.kt`: `@Scope AppScope`, `AppComponent` con `dependencies = [CoreComponent::class]` y `fun inject(activity: MainActivity)`, `ViewModelFactory` con `@Inject constructor` y `object Injector`.
2. `App.kt`: `class App : Application()` que llama `InjectorCore.init()` y luego `Injector.init(InjectorCore.component)`.
3. En `AndroidManifest.xml`: `android:name=".App"` en `<application>`.

### Paso 10. Pantalla
1. `strings.xml` con todos los textos.
2. `activity_main.xml`: `ScrollView` → `LinearLayout` → tres `MaterialCardView` con los ids de la sección 5.
3. `themes.xml`: padre `Theme.Material3.Light` (Material 3 **no** tiene `DarkActionBar`; fue un error real al crear este proyecto).
4. `MainViewModel` con las LiveData y el `init { viewModelScope.launch { … } }`.
5. `MainActivity`: `Injector.component.inject(this)` **antes** de `super.onCreate`, `binding = ActivityMainBinding.inflate(...)`, `viewModels { factory }` y los tres `configurar…()`.

### Paso 11. Compilar y correr
`.\gradlew :app:assembleDebug` y ▶ en Android Studio. Prueba: nombre vacío (error rojo), `1 ÷ 0` (error), cambiar el combo (mensaje verde), rotar el teléfono (el estado se conserva gracias al ViewModel).

## 8. Plantilla para una HU nueva (ejemplo: "como usuario quiero despedirme")

1. **Controller/regla**: `armarDespedida()` en `SaludoController.java` (o directo en Kotlin si es código nuevo).
2. **Caso de uso**: `DespedirUseCase` con `@Inject constructor` en `domain/usecase`.
3. **Exponerlo**: `fun despedirUseCase(): DespedirUseCase` en `CoreComponent`.
4. **Fábrica**: agrégalo al constructor de `ViewModelFactory` y pásalo al ViewModel.
5. **ViewModel**: `_despedida` / `despedida` y `fun despedir(nombre)`.
6. **Layout + strings**: botón `btnDespedir`, texto en `strings.xml`.
7. **Activity**: listener y observer.
8. **Prueba**: un `@Test` en `CasosDeUsoTest`.
9. **Documento de transición** de la HU con las secciones 3, 4 y 5 de este archivo.

## 9. Problemas comunes

| Síntoma | Causa | Solución |
|---|---|---|
| `Unresolved reference: DaggerCoreComponent` | kapt aún no genera el código | Build → Make Project; revisa que el módulo tenga el plugin `kotlin-kapt` |
| `… cannot depend on scoped components` / error de scope | `AppComponent` con `@Singleton` igual que `CoreComponent` | usa un scope propio (`@AppScope`) |
| `lateinit property component has not been initialized` | falta `android:name=".App"` en el manifest, o `inject(this)` antes de `App.onCreate` | revisa el manifest y el orden en `initApplication()` |
| `SDK location not found` | `local.properties` mal escrito | usa barras normales: `sdk.dir=C:/Users/Usuario/AppData/Local/Android/Sdk` (las barras invertidas sin escapar fallan; fue un error real de este proyecto) |
| `Theme.Material3.Light.DarkActionBar not found` | esa variante no existe en Material 3 | usa `Theme.Material3.Light` |
| `NetworkOnMainThreadException` / pantalla congelada | IO en el hilo principal | `withContext(Dispatchers.IO)` en el repositorio |
