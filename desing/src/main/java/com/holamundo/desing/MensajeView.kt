package com.holamundo.desing

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

// Sistema de diseño compartido (en AC Móvil: chips, adapters, diálogos y botones en :desing).
// Funciones de extensión: agregan métodos a TextView sin heredar de ella.

/** Muestra un mensaje de éxito con el color del sistema de diseño. */
fun TextView.mostrarExito(texto: String) {
    text = texto
    setTextColor(ContextCompat.getColor(context, R.color.desing_exito))
    isVisible = true
}

/** Muestra un mensaje de error con el color del sistema de diseño. */
fun TextView.mostrarError(texto: String) {
    text = texto
    setTextColor(ContextCompat.getColor(context, R.color.desing_error))
    isVisible = true
}
