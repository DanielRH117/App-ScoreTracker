package com.daniher.scoretracker

data class CLTableItem(
    var id: String = "",
    var name: String = "",
    var partidosJugados: Int = 0,
    var golesFavor: Int = 0,
    var golesContra: Int = 0,
    var puntos: Int = 0,
    var diferenciaGoles: Int = 0
)