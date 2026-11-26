package com.holamundo.core.domain.repository

import com.holamundo.core.domain.model.OpcionCatalogo

/**
 * Contrato del repositorio. El dominio solo conoce esta interfaz; la implementación
 * (BD local, API remota o ambas) vive en data/. Así funciona offline-first en AC Móvil:
 * la UI lee de la BD local y el sync la llena después.
 */
interface CatalogoRepository {
    suspend fun obtenerLenguajes(): List<OpcionCatalogo>
}
