package com.daniher.scoretracker

data class MatchItem(
    var id: String = "",
    val team1: String,
    val team2: String,
    val time: String,
    val date: String
) {
    constructor() : this("", "", "", "", "")
}