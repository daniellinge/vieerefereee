package com.daniel_linge.viewreferee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TickerScreen(
    navController: NavHostController,
    halfDuration: Int,
    observationViewModel: ObservationViewModel = viewModel(),
    refereeViewModel: RefereeViewModel = viewModel()
) {
    var noteText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedEvent by remember { mutableStateOf<String?>(null) }

    val homeTeam by observationViewModel.homeTeam.observeAsState("")
    val awayTeam by observationViewModel.awayTeam.observeAsState("")
    val remainingTime by refereeViewModel.remainingTime.observeAsState("")
    val halfRemainingTime by refereeViewModel.halfRemainingTime.observeAsState("")
    val events by refereeViewModel.eventList.observeAsState(emptyList())
    val currentReferee by refereeViewModel.currentReferee.observeAsState()

    LaunchedEffect(Unit) {
        refereeViewModel.setHomeTeam(homeTeam)
        refereeViewModel.setAwayTeam(awayTeam)
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        currentReferee?.let { referee ->
        Text("Beobachtung für: ${referee.name} (${referee.id})")
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text("Heimmannschaft: $homeTeam")
        Text("Gastmannschaft: $awayTeam")
        Text("Spielstand: $remainingTime")

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { selectedEvent = "Tor" }) {
                    Text("Tor")
                }
                Button(onClick = { selectedEvent = "Wechsel" }) {
                    Text("Wechsel")
                }
                Button(onClick = {
                    refereeViewModel.addEvent(Event(0, "Kritik: ${noteText.text}", getCurrentTimeString()))
                    noteText = TextFieldValue("")
                }) {
                    Text("Kritik")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    refereeViewModel.addEvent(Event(0, "Lob: ${noteText.text}", getCurrentTimeString()))
                    noteText = TextFieldValue("")
                }) {
                    Text("Lob")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedEvent == "Tor" || selectedEvent == "Wechsel") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (selectedEvent == "Tor") {
                            refereeViewModel.updateScore(true, homeTeam)
                        } else {
                            refereeViewModel.addEvent(Event(0, "$selectedEvent für $homeTeam", getCurrentTimeString()))
                        }
                        selectedEvent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text(homeTeam)
                }
                Button(
                    onClick = {
                        if (selectedEvent == "Tor") {
                            refereeViewModel.updateScore(false, awayTeam)
                        } else {
                            refereeViewModel.addEvent(Event(0, "$selectedEvent für $awayTeam", getCurrentTimeString()))
                        }
                        selectedEvent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(awayTeam)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Notiz hinzufügen") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    refereeViewModel.addEvent(Event(0, "Notiz: ${noteText.text}", getCurrentTimeString()))
                    noteText = TextFieldValue("")

                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            refereeViewModel.setHomeTeam(homeTeam)
            refereeViewModel.setAwayTeam(awayTeam)
            refereeViewModel.startGame(halfDuration)
        }) {
            Text("Spielstart")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { refereeViewModel.endFirstHalf() }) {
                Text("Ende der 1. Halbzeit")
            }
            Button(onClick = {
                refereeViewModel.setHomeTeam(homeTeam)
                refereeViewModel.setAwayTeam(awayTeam)
                refereeViewModel.startSecondHalf()
            }) {
                Text("Start der 2. Halbzeit")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            refereeViewModel.endGame()
        }) {
            Text("Spielende")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(events) { event ->
                Text(event.description)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Restspielzeit: $remainingTime")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Restspielzeit der Halbzeit: $halfRemainingTime")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            observationViewModel.setEvents(events)
            val finalScore = "${refereeViewModel.homeScore.value}:${refereeViewModel.awayScore.value}"
            observationViewModel.setFinalScore(finalScore)
            navController.navigate("summary")
        }) {
            Text("Weiter zur Zusammenfassung")
        }
    }
}

private fun getCurrentTimeString(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
