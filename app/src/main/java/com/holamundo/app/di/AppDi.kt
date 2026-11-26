package com.holamundo.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.holamundo.app.ui.main.MainActivity
import com.holamundo.app.ui.main.MainViewModel
import com.holamundo.core.di.CoreComponent
import com.holamundo.core.domain.usecase.CalcularUseCase
import com.holamundo.core.domain.usecase.ObtenerLenguajesUseCase
import com.holamundo.core.domain.usecase.SaludarUseCase
import dagger.Component
import javax.inject.Inject
import javax.inject.Scope

/** Scope propio del módulo :app (no puede ser @Singleton porque CoreComponent ya lo usa). */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope

/**
 * Grafo de :app. "dependencies = [CoreComponent]" le permite usar lo que :core expone
 * (los casos de uso). Así se conectan los grafos de distintos módulos sin un grafo único.
 */
@AppScope
@Component(dependencies = [CoreComponent::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}

/**
 * Fábrica de ViewModels: Android crea los ViewModel, así que Dagger no puede hacerlo directo.
 * Dagger crea la fábrica con los casos de uso y la fábrica crea el ViewModel.
 */
class ViewModelFactory @Inject constructor(
    private val saludar: SaludarUseCase,
    private val calcular: CalcularUseCase,
    private val obtenerLenguajes: ObtenerLenguajesUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MainViewModel::class.java)) { "ViewModel desconocido: $modelClass" }
        return MainViewModel(saludar, calcular, obtenerLenguajes) as T
    }
}

/** Injector del módulo :app (en AC Móvil: Injector, InjectorMx, InjectorSdm, InjectorTR). */
object Injector {
    lateinit var component: AppComponent
        private set

    fun init(core: CoreComponent) {
        component = DaggerAppComponent.builder().coreComponent(core).build()
    }
}
