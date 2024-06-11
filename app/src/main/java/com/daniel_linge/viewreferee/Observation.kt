package com.daniel_linge.viewreferee

import androidx.annotation.Keep

@Keep
data class Observation(
    val refereeId: String = "", // Standardwert für alle Felder hinzufügen
    val refereeName: String = "",
    val gameDate: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val finalScore: String = "",
    val events: List<Event> = emptyList(),
    val notes: String = ""
) {
    // Leerer Konstruktor für Firestore
    constructor() : this("", "", "", "", "", "", emptyList(), "")
}
@Keep
data class Event(
    val id: Int = 0,
    val description: String = "",
    val time: String = ""
) {
    constructor() : this(0, "", "")
}
