package com.registrocarregamento.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registrocarregamento.data.repository.CarregamentoRepository
import com.registrocarregamento.domain.model.Carregamento
import com.registrocarregamento.domain.model.CarregamentoRascunho
import com.registrocarregamento.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CarregamentoRepository
) : ViewModel() {

    private val _rascunho = MutableStateFlow(CarregamentoRascunho())
    val rascunho: StateFlow<CarregamentoRascunho> = _rascunho.asStateFlow()

    private val _idSalvo = MutableStateFlow<Long?>(null)
    val idSalvo: StateFlow<Long?> = _idSalvo.asStateFlow()

    val pendentes: StateFlow<Int> = repository.contarPendentes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val historico = repository.listarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun atualizarPlaca(v: String) = _rascunho.update { it.copy(placa = v) }
    fun atualizarCliente(v: String) = _rascunho.update { it.copy(cliente = v) }
    fun atualizarCidade(v: String) = _rascunho.update { it.copy(cidadeCarregamento = v) }
    fun atualizarCidadeConfereCte(v: Boolean) = _rascunho.update { it.copy(cidadeConfereCte = v) }
    fun atualizarFotoPlaca(path: String) = _rascunho.update { it.copy(fotoPlacaPath = path) }
    fun atualizarFotoNfe1(path: String) = _rascunho.update { it.copy(fotoNfe1Path = path) }
    fun atualizarFotoNfe2(path: String) = _rascunho.update { it.copy(fotoNfe2Path = path) }
    fun atualizarFotoCte(path: String) = _rascunho.update { it.copy(fotoCtePath = path) }

    fun limparTudo() { _rascunho.value = CarregamentoRascunho(); _idSalvo.value = null }

    fun processar() {
        val r = _rascunho.value
        viewModelScope.launch {
            val id = repository.salvar(
                Carregamento(
                    placa = r.placa,
                    cliente = r.cliente,
                    cidadeCarregamento = r.cidadeCarregamento,
                    cidadeConfereCte = r.cidadeConfereCte,
                    dataRegistro = DateUtil.dataAtual(),
                    horaRegistro = DateUtil.horaAtual(),
                    fotoPlaca = r.fotoPlacaPath ?: "",
                    fotoNfe1 = r.fotoNfe1Path ?: "",
                    fotoNfe2 = r.fotoNfe2Path,
                    fotoCte = r.fotoCtePath ?: ""
                )
            )
            _idSalvo.value = id
        }
    }

    fun isValido(): Boolean {
        val r = _rascunho.value
        return r.placa.isNotBlank() &&
                r.cliente.isNotBlank() &&
                r.cidadeCarregamento.isNotBlank() &&
                r.fotoPlacaPath != null &&
                r.fotoNfe1Path != null &&
                r.fotoCtePath != null
    }

    fun apagarHistorico() {
        viewModelScope.launch {
            repository.apagarTodos()
        }
    }
}
