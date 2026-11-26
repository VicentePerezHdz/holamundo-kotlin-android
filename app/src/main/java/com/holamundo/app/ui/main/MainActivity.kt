package com.holamundo.app.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.holamundo.app.databinding.ActivityMainBinding
import com.holamundo.app.di.Injector
import com.holamundo.app.di.ViewModelFactory
import com.holamundo.core.domain.model.OpcionCatalogo
import com.holamundo.core.domain.model.Operacion
import com.holamundo.core.domain.model.Resultado
import com.holamundo.desing.mostrarError
import com.holamundo.desing.mostrarExito
import javax.inject.Inject

/**
 * La Activity solo pinta y reenvía eventos al ViewModel. Nada de reglas de negocio aquí.
 * ViewBinding: ActivityMainBinding se genera de activity_main.xml (adiós findViewById).
 */
class MainActivity : AppCompatActivity() {

    @Inject lateinit var factory: ViewModelFactory   // Dagger lo llena en inject(this)

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.component.inject(this) // antes de super.onCreate y de usar viewModel
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        aplicarMargenesDeSistema()

        configurarSaludo()
        configurarCalculadora()
        configurarCombo()
    }

    /** Android 15 (targetSdk 35) dibuja edge-to-edge: separamos el contenido de la barra de estado y de navegación. */
    private fun aplicarMargenesDeSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.raiz) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
    }

    private fun configurarSaludo() {
        binding.btnSaludar.setOnClickListener { viewModel.saludar(binding.etNombre.text.toString()) }
        viewModel.saludo.observe(this) { pintar(binding.tvSaludo, it) }
    }

    private fun configurarCalculadora() {
        binding.spOperacion.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Operacion.entries.map { it.simbolo }
        )
        binding.btnCalcular.setOnClickListener {
            viewModel.calcular(
                binding.etNumeroA.text.toString(),
                binding.etNumeroB.text.toString(),
                Operacion.entries[binding.spOperacion.selectedItemPosition]
            )
        }
        viewModel.calculo.observe(this) { pintar(binding.tvResultado, it) }
    }

    private fun configurarCombo() {
        viewModel.lenguajes.observe(this) { lista ->
            binding.spLenguaje.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, lista)
        }
        binding.spLenguaje.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.seleccionarLenguaje(parent.getItemAtPosition(position) as OpcionCatalogo)
            }
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
        viewModel.mensajeLenguaje.observe(this) { binding.tvMensajeLenguaje.mostrarExito(it) }
    }

    /** when sobre una sealed class: el compilador obliga a cubrir Exito y Error. */
    private fun pintar(destino: TextView, resultado: Resultado<String>) = when (resultado) {
        is Resultado.Exito -> destino.mostrarExito(resultado.valor)
        is Resultado.Error -> destino.mostrarError(resultado.mensaje)
    }
}
