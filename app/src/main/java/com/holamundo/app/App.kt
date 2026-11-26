package com.holamundo.app

import android.app.Application
import com.holamundo.app.di.Injector
import com.holamundo.core.di.InjectorCore

/**
 * Igual que App.kt de AC Móvil: al arrancar la app se inicializan los injectors de cada módulo,
 * en orden de dependencia (primero :core, después :app que depende de él).
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initApplication()
    }

    private fun initApplication() {
        InjectorCore.init()
        Injector.init(InjectorCore.component)
    }
}
