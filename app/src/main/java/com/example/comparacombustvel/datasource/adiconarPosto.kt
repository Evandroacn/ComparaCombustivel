package com.example.comparacombustvel.datasource

import com.example.comparacombustvel.model.Posto
import com.example.comparacombustvel.R
import android.content.Context
import java.util.Date
import java.util.UUID

object PostoManager {

    fun savePosto(
        // 1. Dados que vêm da UI
        context: Context,
        alcool: String,
        gasolina: String,
        posto: String,
        localizacao: String,
        checked: Boolean,

        // 2. Contexto de Estado e Dependências
        isEditing: Boolean,
        postoId: String?,
        repository: PostoRepository,

        // 3. Callback para retornar o resultado
        onResult: (success: Boolean, message: String) -> Unit
    ) {

        // --- 1. Cria ou Atualiza o objeto Posto ---
        val porcentagemInt = if (checked) 75 else 70

        val dataCadastro = if (isEditing && postoId != null) {
            repository.getPostoById(postoId)?.dataCadastro ?: Date().time
        } else {
            Date().time
        }
        val id = if (isEditing && postoId != null) postoId else UUID.randomUUID().toString()

        val postoParaSalvar = Posto(
            id = id,
            nome = posto.trim(),
            precoAlcool = alcool, // A View já garantiu que não está em branco
            precoGasolina = gasolina, // A View já garantiu que não está em branco
            localizacao = localizacao.trim().takeIf { it.isNotEmpty() },
            dataCadastro = dataCadastro,
            porcentagemCalculo = porcentagemInt
        )

        // --- 2. Chama o Repository ---
        try {
            if (isEditing) {
                repository.updatePosto(postoParaSalvar)
            } else {
                repository.addPosto(postoParaSalvar)
            }
            onResult(true, context.getString(R.string.msg_save_success))
        } catch (e: Exception) {
            onResult(false, context.getString(R.string.msg_save_error, e.message))
        }
    }
}