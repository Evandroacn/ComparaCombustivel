package com.example.comparacombustvel.datasource

object Calculations {
    fun calculate(
        alcool: Double,
        gasolina: Double,
        posto: String,
        porcentagem: Boolean
    ): String {
        val porct: Double = if (porcentagem) 0.75 else 0.70
        val limite = gasolina * porct

        val resultMessage: String = if (posto.isNotBlank()) {
            if (alcool > limite) "A Gasolina é mais rentável no posto $posto" else "O Álcool é mais rentável no posto $posto"
        } else {
            if (alcool > limite) "A Gasolina é mais rentável" else "O Álcool é mais rentável"
        }

        return resultMessage
    }
}