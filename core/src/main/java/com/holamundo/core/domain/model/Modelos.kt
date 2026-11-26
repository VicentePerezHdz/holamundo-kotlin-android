package com.holamundo.core.domain.model

/** Operaciones de la calculadora. El símbolo es lo que ve el usuario en el Spinner. */
enum class Operacion(val simbolo: String) {
    SUMA("+"), RESTA("−"), MULTIPLICACION("×"), DIVISION("÷")
}

/** Opción del combobox (Spinner): misma idea que OpcionCatalogo de la API .NET. */
data class OpcionCatalogo(val clave: String, val texto: String, val mensaje: String) {
    // El Spinner usa toString() para pintar cada renglón.
    override fun toString(): String = texto
}

/**
 * Resultado de una operación de negocio: éxito o error con mensaje.
 * Es el equivalente Kotlin de devolver 200 vs ProblemDetails en .NET.
 */
sealed class Resultado<out T> {
    data class Exito<T>(val valor: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}
