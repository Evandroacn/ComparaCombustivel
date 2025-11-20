package com.example.comparacombustvel.datasource

import android.content.Context
import com.example.comparacombustvel.R

object Calculations {
    fun calculate(
        context: Context, // <--- Recebe Contexto
        alcool: Double,
        gasolina: Double,
        posto: String,
        porcentagem: Boolean
    ): String {
        val porct: Double = if (porcentagem) 0.75 else 0.70
        val limite = gasolina * porct

        // Se alcool for MAIOR que o limite, ele está caro -> Gasolina compensa
        // Se alcool for MENOR ou IGUAL, ele está barato -> Álcool compensa

        val resultMessage: String = if (posto.isNotBlank()) {
            if (alcool <= limite)
                context.getString(R.string.calc_alcohol_better_station, posto)
            else
                context.getString(R.string.calc_gas_better_station, posto)
        } else {
            if (alcool <= limite)
                context.getString(R.string.calc_alcohol_better)
            else
                context.getString(R.string.calc_gas_better)
        }

        return resultMessage
    }
}