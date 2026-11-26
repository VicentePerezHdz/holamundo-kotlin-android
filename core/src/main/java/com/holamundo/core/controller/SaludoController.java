package com.holamundo.core.controller;

import javax.inject.Inject;

/**
 * Estilo "legado" de AC Móvil: lógica de negocio en un Controller .java.
 * Kotlin lo usa sin problema (interoperabilidad Java ↔ Kotlin).
 */
public class SaludoController {

    public static final int MIN_CARACTERES = 2;

    @Inject
    public SaludoController() {
    }

    /** Devuelve null si el nombre es válido, o el mensaje de error. */
    public String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (nombre.trim().length() < MIN_CARACTERES) {
            return "El nombre debe tener al menos " + MIN_CARACTERES + " caracteres.";
        }
        return null;
    }

    public String armarSaludo(String nombre) {
        return "¡Hola, " + nombre.trim() + "! Saludo armado en Java y mostrado con Kotlin.";
    }
}
