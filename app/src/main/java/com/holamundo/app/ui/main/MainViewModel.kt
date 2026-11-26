package com.holamundo.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holamundo.core.domain.model.OpcionCatalogo
import com.holamundo.core.domain.model.Operacion
import com.holamundo.core.domain.model.Resultado
import com.holamundo.core.domain.usecase.CalcularUseCase
import com.holamundo.core.domain.usecase.ObtenerLenguajesUseCase
import com.holamundo.core.domain.usecase.SaludarUseCase
import kotlinx.coroutines.launch

/**
 * El ViewModel guarda el estado de la pantalla y sobrevive a la rotación.
 * Expone LiveData de solo lectura; la Activity las observa y pinta.
 */
class MainViewModel(
    private val saludarUseCase: SaludarUseCase,
    private val calcularUseCase: CalcularUseCase,
    private val obtenerLenguajesUseCase: ObtenerLenguajesUseCase
) : ViewModel() {

    // Patrón: MutableLiveData privada (se escribe aquí) + LiveData pública (solo se lee afuera).
    private val _saludo = MutableLiveData<Resultado<String>>()
    val saludo: LiveData<Resultado<String>> = _saludo

    private val _calculo = MutableLiveData<Resultado<String>>()
    val calculo: LiveData<Resultado<String>> = _calculo

    private val _lenguajes = MutableLiveData<List<OpcionCatalogo>>()
    val lenguajes: LiveData<List<OpcionCatalogo>> = _lenguajes

    private val _mensajeLenguaje = MutableLiveData<String>()
    val mensajeLenguaje: LiveData<String> = _mensajeLenguaje

    init {
        // viewModelScope: la coroutine se cancela sola si el ViewModel se destruye.
        viewModelScope.launch { _lenguajes.value = obtenerLenguajesUseCase() }
    }

    fun saludar(nombre: String) {
        _saludo.value = saludarUseCase(nombre)
    }

    fun calcular(a: String, b: String, operacion: Operacion) {
        _calculo.value = calcularUseCase(a, b, operacion)
    }

    fun seleccionarLenguaje(opcion: OpcionCatalogo) {
        _mensajeLenguaje.value = opcion.mensaje
    }
}
