package com.example.viewreferee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun RefereeObservationsScreen(
    navController: NavHostController,
    refereeViewModel: RefereeViewModel = viewModel()
) {
    val currentReferee by refereeViewModel.currentReferee.observeAsState()
    var observations by remember { mutableStateOf(listOf<Observation>()) }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Load all observations for the referee on compose initialization
    LaunchedEffect(currentReferee?.id) {
        currentReferee?.id?.let { refereeId ->
            FirestoreHelper.fetchObservations(onSuccess = { list ->
                observations = list.filter { it.refereeId == refereeId }
                isLoading = false
            }, onFailure = {
                showError = true
                isLoading = false
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Beobachtungen von ${currentReferee?.name}")
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (showError) {
                Text("Fehler beim Laden der Beobachtungen")
            } else {
                LazyColumn {
                    items(observations) { observation ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Spiel: ${observation.homeTeam} vs ${observation.awayTeam}")
                            Text("Datum: ${observation.gameDate}")
                            Text("Endstand: ${observation.finalScore}")
                            Text("Notizen: ${observation.notes}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("start_screen") }) {
            Text("Neue Beobachtung")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("refereeCapture") }) {
            Text("Zurück")
        }
    }
}
