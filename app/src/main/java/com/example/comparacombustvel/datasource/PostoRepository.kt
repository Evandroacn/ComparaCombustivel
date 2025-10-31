package com.example.comparacombustvel.datasource

import android.content.Context
import android.content.SharedPreferences
import com.example.comparacombustvel.model.Posto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostoRepository(context: Context) {

    private val gson = Gson()
    private val PREFS_NAME = "postos_prefs"
    private val LISTA_POSTOS_KEY = "lista_postos_json"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // LÊ a lista completa
    fun getAllPostos(): List<Posto> {
        val json = sharedPreferences.getString(LISTA_POSTOS_KEY, null)
        if (json.isNullOrEmpty()) {
            return emptyList()
        }
        try {
            // Define o tipo: uma Lista de Postos
            val type = object : TypeToken<List<Posto>>() {}.type
            // Converte o JSON de volta para a lista de objetos
            return gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // SALVA a lista completa (usado internamente)
    private fun savePostosList(postos: List<Posto>) {
        // Converte a lista de objetos para uma string JSON
        val json = gson.toJson(postos)
        // Salva a string JSON no SharedPreferences
        sharedPreferences.edit().putString(LISTA_POSTOS_KEY, json).apply()
    }

    // ADICIONA um novo posto
    fun addPosto(posto: Posto) {
        val currentList = getAllPostos().toMutableList()
        currentList.add(posto)
        savePostosList(currentList)
    }

    // ATUALIZA um posto existente (encontra pelo ID)
    fun updatePosto(updatedPosto: Posto) {
        val currentList = getAllPostos().toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedPosto.id }
        if (index != -1) {
            currentList[index] = updatedPosto
            savePostosList(currentList)
        }
    }

    // DELETA um posto (pelo ID)
    fun deletePosto(id: String) {
        val currentList = getAllPostos().toMutableList()
        currentList.removeAll { it.id == id }
        savePostosList(currentList)
    }

    // BUSCA um posto específico pelo ID
    fun getPostoById(id: String): Posto? {
        return getAllPostos().find { it.id == id }
    }
}