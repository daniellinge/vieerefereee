package com.example.viewreferee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StartScreen(
    navController: NavHostController,
    refereeViewModel: RefereeViewModel = viewModel() ,
    observationViewModel: ObservationViewModel = viewModel()
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Calendar.getInstance().time)
    var homeTeam by remember { mutableStateOf(TextFieldValue("")) }
    var awayTeam by remember { mutableStateOf(TextFieldValue("")) }
    var gameDate by remember { mutableStateOf(TextFieldValue(currentDate)) }
    val showError by remember { mutableStateOf(false) }
    val showSuccess by remember { mutableStateOf(false) }

    // Beobachten Sie den aktuellen Schiedsrichter
    val currentReferee by refereeViewModel.currentReferee.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Neue Beobachtung")
        Spacer(modifier = Modifier.height(16.dp))

        currentReferee?.let { referee ->
            OutlinedTextField(
                value = referee.name,
                onValueChange = { /* Keine Änderungen erlaubt */ },
                label = { Text("Name des Schiedsrichters") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Setzt das Textfeld auf schreibgeschützt
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = homeTeam,
            onValueChange = { homeTeam = it },
            label = { Text("Heimmannschaft") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = awayTeam,
            onValueChange = { awayTeam = it },
            label = { Text("Gastmannschaft") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = gameDate,
            onValueChange = { gameDate = it },
            label = { Text("Spieldatum") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Hide keyboard if needed */ }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        var selectedCategory by remember { mutableStateOf(30) } // Default value for D Jugend

        Text("Spielart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        selectedCategory = 30 // D Jugend 2* 30 Min
                        observationViewModel.setHomeTeam(homeTeam.text)
                        observationViewModel.setAwayTeam(awayTeam.text)
                        observationViewModel.setGameDate(gameDate.text)
                        navController.navigate("ticker_screen/$selectedCategory")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("D Jugend 2* 30 Min")
                }
                Button(
                    onClick = {
                        selectedCategory = 35 // C Jugend 2* 35 Min
                        observationViewModel.setHomeTeam(homeTeam.text)
                        observationViewModel.setAwayTeam(awayTeam.text)
                        observationViewModel.setGameDate(gameDate.text)
                        navController.navigate("ticker_screen/$selectedCategory")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("C Jugend 2* 35 Min")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        selectedCategory = 40 // B Jugend 2* 40 Min
                        observationViewModel.setHomeTeam(homeTeam.text)
                        observationViewModel.setAwayTeam(awayTeam.text)
                        observationViewModel.setGameDate(gameDate.text)
                        navController.navigate("ticker_screen/$selectedCategory")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("B Jugend 2* 40 Min")
                }
                Button(
                    onClick = {
                        selectedCategory = 45 // Herren 2* 45 Min
                        observationViewModel.setHomeTeam(homeTeam.text)
                        observationViewModel.setAwayTeam(awayTeam.text)
                        observationViewModel.setGameDate(gameDate.text)
                        navController.navigate("ticker_screen/$selectedCategory")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Herren 2* 45 Min")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fehler beim Speichern der Beobachtung")
        }

        if (showSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Beobachtung erfolgreich gespeichert!")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("referee_observations") }) {
            Text("Zurück")
        }
    }
}
