package com.holamundo.core.data.local

import com.holamundo.core.domain.model.OpcionCatalogo
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Simula la tabla local (en AC Móvil sería un DAO de ORMLite sobre SQLite en
 * core/data/local, llenado por el sync con DownloadDbResponse).
 */
class CatalogoLocalDataSource @Inject constructor() {

    suspend fun leerLenguajes(): List<OpcionCatalogo> {
        delay(100) // simula lectura de disco
        return listOf(
            OpcionCatalogo("kotlin", "Kotlin / Android", "ViewModel + coroutines + Dagger 2, como AC Móvil."),
            OpcionCatalogo("java", "Java (legado)", "Controllers .java con lógica de negocio que conviven con Kotlin."),
            OpcionCatalogo("csharp", "C# / .NET 8", "La API que consumirían las apps en línea."),
            OpcionCatalogo("sql", "SQLite / ORMLite", "Persistencia offline-first sincronizada periódicamente.")
        )
    }
}
