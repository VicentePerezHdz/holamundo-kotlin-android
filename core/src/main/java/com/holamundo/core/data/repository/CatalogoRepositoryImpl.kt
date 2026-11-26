package com.holamundo.core.data.repository

import com.holamundo.core.data.local.CatalogoLocalDataSource
import com.holamundo.core.domain.model.OpcionCatalogo
import com.holamundo.core.domain.repository.CatalogoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Implementación: lee de la fuente local en el hilo de IO, nunca en el hilo principal. */
class CatalogoRepositoryImpl @Inject constructor(
    private val local: CatalogoLocalDataSource
) : CatalogoRepository {

    override suspend fun obtenerLenguajes(): List<OpcionCatalogo> =
        withContext(Dispatchers.IO) { local.leerLenguajes() }
}
