package com.holamundo.core.di

import com.holamundo.core.data.repository.CatalogoRepositoryImpl
import com.holamundo.core.domain.repository.CatalogoRepository
import com.holamundo.core.domain.usecase.CalcularUseCase
import com.holamundo.core.domain.usecase.ObtenerLenguajesUseCase
import com.holamundo.core.domain.usecase.SaludarUseCase
import dagger.Binds
import dagger.Component
import dagger.Module
import javax.inject.Singleton

/** @Binds: "cuando alguien pida CatalogoRepository, entrégale CatalogoRepositoryImpl". */
@Module
abstract class CoreModule {
    @Binds
    @Singleton
    abstract fun bindCatalogoRepository(impl: CatalogoRepositoryImpl): CatalogoRepository
}

/**
 * Grafo de Dagger del módulo :core. Expone (métodos de provisión) lo que otros módulos pueden usar.
 * En AC Móvil no hay un grafo único: cada módulo tiene el suyo (Injector, InjectorMx, InjectorSdm, InjectorTR).
 */
@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {
    fun saludarUseCase(): SaludarUseCase
    fun calcularUseCase(): CalcularUseCase
    fun obtenerLenguajesUseCase(): ObtenerLenguajesUseCase
}

/** Injector del módulo :core. Se inicializa una sola vez desde App.initApplication(). */
object InjectorCore {
    lateinit var component: CoreComponent
        private set

    fun init() {
        if (!::component.isInitialized) component = DaggerCoreComponent.create()
    }
}
