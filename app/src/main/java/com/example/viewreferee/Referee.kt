package com.example.viewreferee

import java.util.UUID

data class Referee(
    val id: String = UUID.randomUUID().toString(), // Generiert eine eindeutige ID
    val name: String = "",
    var observationCount: Int = 0,
    val lastObservationDate: String? = null

)
