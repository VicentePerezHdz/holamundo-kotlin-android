package com.holamundo.core

import com.holamundo.core.controller.SaludoController
import com.holamundo.core.domain.model.OpcionCatalogo
import com.holamundo.core.domain.model.Operacion
import com.holamundo.core.domain.model.Resultado
import com.holamundo.core.domain.repository.CatalogoRepository
import com.holamundo.core.domain.usecase.CalcularUseCase
import com.holamundo.core.domain.usecase.ObtenerLenguajesUseCase
import com.holamundo.core.domain.usecase.SaludarUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas unitarias en JVM (sin emulador): ./gradlew :core:testDebugUnitTest */
class CasosDeUsoTest {

    private val calcular = CalcularUseCase()
    private val saludar = SaludarUseCase(SaludoController())

    @Test
    fun `suma devuelve la expresion completa`() {
        assertEquals(Resultado.Exito("10 + 5 = 15"), calcular("10", "5", Operacion.SUMA))
    }

    @Test
    fun `division entre cero devuelve error de negocio`() {
        assertEquals(Resultado.Error("No se puede dividir entre cero."), calcular("1", "0", Operacion.DIVISION))
    }

    @Test
    fun `nombre vacio no pasa la validacion del controller java`() {
        assertTrue(saludar("  ") is Resultado.Error)
    }

    @Test
    fun `lenguajes se leen del repositorio`() = runBlocking {
        // Repositorio falso: así se prueba el caso de uso sin base de datos.
        val falso = object : CatalogoRepository {
            override suspend fun obtenerLenguajes() = listOf(OpcionCatalogo("k", "Kotlin", "msg"))
        }
        assertEquals(1, ObtenerLenguajesUseCase(falso)().size)
    }
}
