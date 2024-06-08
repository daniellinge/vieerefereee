package com.example.viewreferee

import androidx.annotation.Keep

@Keep
data class Observation(
    val refereeId: String = "",
    val refereeName: String = "",
    val gameDate: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val finalScore: String = "",
    val events: List<Event> = emptyList(),
    val notes: String = ""
)

@Keep
data class Event(
    val id: Int = 0,
    val description: String = "",
    val time: String = ""
)
