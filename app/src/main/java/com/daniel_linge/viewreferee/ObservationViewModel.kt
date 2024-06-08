package com.daniel_linge.viewreferee

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ObservationViewModel : ViewModel() {

    private val _homeTeam = MutableLiveData<String>()
    val homeTeam: LiveData<String> get() = _homeTeam

    private val _awayTeam = MutableLiveData<String>()
    val awayTeam: LiveData<String> get() = _awayTeam

    private val _gameDate = MutableLiveData<String>()
    val gameDate: LiveData<String> get() = _gameDate

    private val _notes = MutableLiveData<String>()
    val notes: LiveData<String> get() = _notes

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _finalScore = MutableLiveData<String>()
    val finalScore: LiveData<String> get() = _finalScore

    fun setHomeTeam(homeTeam: String) {
        _homeTeam.value = homeTeam
    }

    fun setAwayTeam(awayTeam: String) {
        _awayTeam.value = awayTeam
    }

    fun setGameDate(gameDate: String) {
        _gameDate.value = gameDate
    }

    fun setNotes(notes: String) {
        _notes.value = notes
    }

    fun setEvents(events: List<Event>) {
        _events.value = events
    }

    fun setFinalScore(finalScore: String) {
        _finalScore.value = finalScore
    }

    fun addEvent(event: Event) {
        _events.value = _events.value.orEmpty() + event
    }

    fun clearObservation() {
        _homeTeam.value = ""
        _awayTeam.value = ""
        _gameDate.value = ""
        _notes.value = ""
        _events.value = emptyList()
        _finalScore.value = ""
    }
}
