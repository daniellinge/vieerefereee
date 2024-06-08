package com.example.viewreferee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun SummaryScreen(
    navController: NavHostController,
    observationViewModel: ObservationViewModel = viewModel(),
    refereeViewModel: RefereeViewModel = viewModel()
) {
    val homeTeam by observationViewModel.homeTeam.observeAsState("")
    val awayTeam by observationViewModel.awayTeam.observeAsState("")
    val gameDate by observationViewModel.gameDate.observeAsState("")
    val finalScore by observationViewModel.finalScore.observeAsState("")
    val events by observationViewModel.events.observeAsState(emptyList())
    val currentReferee by refereeViewModel.currentReferee.observeAsState()

    var summaryText by remember { mutableStateOf(TextFieldValue(observationViewModel.notes.value ?: "")) }
    var showConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Zusammenfassung")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Schiedsrichter: ${currentReferee?.name}")
        Text("Heimmannschaft: $homeTeam")
        Text("Gastmannschaft: $awayTeam")
        Text("Datum: $gameDate")
        Text("Spielstand: $finalScore")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(events) { event ->
                Text(event.description)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = summaryText,
            onValueChange = { summaryText = it },
            label = { Text("Fazit hinzufügen") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            observationViewModel.setNotes(summaryText.text)
            val updatedObservation = Observation(
                refereeId = currentReferee?.id ?: "",
                refereeName = currentReferee?.name ?: "Unbekannt",
                gameDate = gameDate,
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                finalScore = finalScore,
                events = events,
                notes = summaryText.text
            )
            FirestoreHelper.saveObservation(updatedObservation, {
                showConfirmation = true
                observationViewModel.clearObservation()
                refereeViewModel.clearReferee()
            }, {
                // Fehler beim Speichern
            })
        }) {
            Text("Zusammenfassung speichern")
        }

        if (showConfirmation) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Spielbericht wurde erfolgreich übertragen!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("refereeCapture") }) {
            Text("Zurück zur Schiedsrichterauswahl")
        }
    }
}
