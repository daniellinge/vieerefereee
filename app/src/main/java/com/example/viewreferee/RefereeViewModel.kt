package com.example.viewreferee

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class RefereeViewModel : ViewModel() {

    private val _currentReferee = MutableLiveData<Referee?>()
    val currentReferee: LiveData<Referee?> get() = _currentReferee

    private val _homeTeam = MutableLiveData<String>()
    val homeTeam: LiveData<String> get() = _homeTeam

    private val _awayTeam = MutableLiveData<String>()
    val awayTeam: LiveData<String> get() = _awayTeam

    private val _homeScore = MutableLiveData<Int>()
    val homeScore: LiveData<Int> get() = _homeScore

    private val _awayScore = MutableLiveData<Int>()
    val awayScore: LiveData<Int> get() = _awayScore

    private val _remainingTime = MutableLiveData<String>()
    val remainingTime: LiveData<String> get() = _remainingTime

    private val _halfRemainingTime = MutableLiveData<String>()
    val halfRemainingTime: LiveData<String> get() = _halfRemainingTime

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _observation = MutableLiveData<Observation>()
    val observation: LiveData<Observation> get() = _observation

    private var timer: Timer? = null
    private var halfDuration: Int = 45
    private var elapsedTime: Long = 0
    private var totalElapsedTime: Long = 0

    fun setCurrentReferee(referee: Referee?) {
        _currentReferee.value = referee
    }

    fun setHomeTeam(name: String) {
        _homeTeam.value = name
    }

    fun setAwayTeam(name: String) {
        _awayTeam.value = name
    }

    fun setObservation(observation: Observation) {
        _observation.value = observation
        Log.d("RefereeViewModel", "Observation set: $observation")
    }

    fun startGame(halfDuration: Int) {
        this.halfDuration = halfDuration
        elapsedTime = 0
        totalElapsedTime = 0

        val homeTeamValue = _homeTeam.value ?: ""
        val awayTeamValue = _awayTeam.value ?: ""

        addEvent(Event(0, "Start des Spieles $homeTeamValue gegen $awayTeamValue startet um ${getCurrentTimeString()}", getCurrentTimeString()))

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                elapsedTime += 1
                totalElapsedTime += 1
                val halfRemaining = halfDuration * 60 - elapsedTime
                val totalRemaining = 2 * halfDuration * 60 - totalElapsedTime
                val halfMinutes = TimeUnit.SECONDS.toMinutes(halfRemaining)
                val halfSeconds = halfRemaining % 60
                val totalMinutes = TimeUnit.SECONDS.toMinutes(totalRemaining)
                val totalSeconds = totalRemaining % 60
                _halfRemainingTime.postValue(String.format("%02d:%02d", halfMinutes, halfSeconds))
                _remainingTime.postValue(String.format("%02d:%02d", totalMinutes, totalSeconds))
            }
        }, 0, 1000)
    }

    fun endFirstHalf() {
        timer?.cancel()
        elapsedTime = 0
        addEvent(Event(0, "Ende der 1. Halbzeit", getCurrentTimeString()))
    }

    fun startSecondHalf() {
        elapsedTime = 0
        val homeTeamValue = _homeTeam.value ?: ""
        val awayTeamValue = _awayTeam.value ?: ""
        addEvent(Event(0, "Beginn der 2. Halbzeit $homeTeamValue gegen $awayTeamValue um ${getCurrentTimeString()}", getCurrentTimeString()))
        startGame(halfDuration)
    }

    fun endGame() {
        timer?.cancel()
        addEvent(Event(0, "Spielende um ${getCurrentTimeString()}", getCurrentTimeString()))
    }

    fun addEvent(event: Event) {
        _eventList.value = _eventList.value.orEmpty() + event
    }

    fun updateScore(isHomeTeam: Boolean, teamName: String) {
        if (isHomeTeam) {
            _homeScore.value = (_homeScore.value ?: 0) + 1
            addEvent(Event(0, "Tor für $teamName", getCurrentTimeString()))
        } else {
            _awayScore.value = (_awayScore.value ?: 0) + 1
            addEvent(Event(0, "Tor für $teamName", getCurrentTimeString()))
        }
    }

    fun clearReferee() {
        _currentReferee.value = null
    }

    private fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
