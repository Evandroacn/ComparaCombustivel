package com.example.comparacombustvel.model

import java.util.UUID
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class Posto(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val precoAlcool: String,
    val precoGasolina: String,
    val localizacao: String?,
    val dataCadastro: Long = Date().time,
    val porcentagemCalculo: Int
) {
    // Função para exibir a data bonitinha
    fun dataFormatada(): String {
        val saveDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return saveDate.format(Date(dataCadastro))
    }
}