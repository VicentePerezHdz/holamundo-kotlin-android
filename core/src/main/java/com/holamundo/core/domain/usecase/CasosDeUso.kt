package com.holamundo.core.domain.usecase

import com.holamundo.core.controller.SaludoController
import com.holamundo.core.domain.model.OpcionCatalogo
import com.holamundo.core.domain.model.Operacion
import com.holamundo.core.domain.model.Resultado
import com.holamundo.core.domain.repository.CatalogoRepository
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

// Casos de uso: una clase por acción de negocio, con un único método operator fun invoke().
// El ViewModel los llama como si fueran funciones: saludar("Vicente").

/** Valida con el Controller Java y arma el saludo. */
class SaludarUseCase @Inject constructor(private val controller: SaludoController) {
    operator fun invoke(nombre: String): Resultado<String> {
        val error = controller.validarNombre(nombre)
        return if (error != null) Resultado.Error(error) else Resultado.Exito(controller.armarSaludo(nombre))
    }
}

/** Calculadora. Usa BigDecimal (como decimal en C#) para no perder precisión. */
class CalcularUseCase @Inject constructor() {
    operator fun invoke(textoA: String, textoB: String, operacion: Operacion): Resultado<String> {
        val a = textoA.toBigDecimalOrNull() ?: return Resultado.Error("El número A no es válido.")
        val b = textoB.toBigDecimalOrNull() ?: return Resultado.Error("El número B no es válido.")

        val resultado = when (operacion) {
            Operacion.SUMA -> a + b
            Operacion.RESTA -> a - b
            Operacion.MULTIPLICACION -> a * b
            Operacion.DIVISION -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) return Resultado.Error("No se puede dividir entre cero.")
                a.divide(b, 4, RoundingMode.HALF_UP)
            }
        }.stripTrailingZeros().toPlainString()

        return Resultado.Exito("${a.toPlainString()} ${operacion.simbolo} ${b.toPlainString()} = $resultado")
    }
}

/** Obtiene las opciones del combobox desde el repositorio. */
class ObtenerLenguajesUseCase @Inject constructor(private val repository: CatalogoRepository) {
    suspend operator fun invoke(): List<OpcionCatalogo> = repository.obtenerLenguajes()
}
