package com.example.viewreferee

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun RefereeCaptureScreen(
    navController: NavHostController,
    refereeViewModel: RefereeViewModel = viewModel()
) {
    var refereeName by remember { mutableStateOf(TextFieldValue("")) }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var referees by remember { mutableStateOf(listOf<Referee>()) }
    var dbConnectionStatus by remember { mutableStateOf("Checking...") }
    var showDialog by remember { mutableStateOf(false) }
    var refereeToDelete by remember { mutableStateOf<Referee?>(null) }

    // Load all referees on compose initialization
    LaunchedEffect(Unit) {
        FirestoreHelper.fetchAllRefereesWithDetails(onSuccess = { list: List<Referee> ->
            referees = list
            dbConnectionStatus = "Connected"
        }, onFailure = {
            dbConnectionStatus = "Not Connected"
            showError = true
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Liste der Schiedsrichter:")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Name", modifier = Modifier.weight(1f))
            Text("Beobachtungen", modifier = Modifier.weight(1f))
            Text("Letzte Beobachtung", modifier = Modifier.weight(1f))
            Text("Löschen", modifier = Modifier.weight(1f))
        }

        LazyColumn {
            items(referees) { referee: Referee ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            refereeViewModel.setCurrentReferee(referee)
                            navController.navigate("referee_observations")
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(referee.name, modifier = Modifier.weight(1f))
                    Text(referee.observationCount.toString(), modifier = Modifier.weight(1f))
                    Text(referee.lastObservationDate ?: "N/A", modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            refereeToDelete = referee
                            showDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Referee")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Schiedsrichter erfassen")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = refereeName,
            onValueChange = { refereeName = it },
            label = { Text("Name des Schiedsrichters") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Hide keyboard if needed */ }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                FirestoreHelper.fetchRefereeByName(refereeName.text, { exists: Boolean ->
                    if (exists) {
                        showError = true
                        showSuccess = false
                    } else {
                        val newReferee = Referee(name = refereeName.text)
                        FirestoreHelper.saveReferee(newReferee, {
                            // Directly add the new referee to the list
                            referees = referees + newReferee
                            showSuccess = true
                            showError = false
                            refereeName = TextFieldValue("") // Clear the text field
                        }, {
                            showError = true
                            showSuccess = false
                        })
                    }
                }, {
                    showError = true
                    showSuccess = false
                })
            }) {
                Text("Schiedsrichter erfassen")
            }

            Button(onClick = {
                FirestoreHelper.fetchAllRefereesWithDetails(onSuccess = { list: List<Referee> ->
                    referees = list
                }, onFailure = {
                    showError = true
                })
            }) {
                Text("Aktualisieren")
            }
        }

        if (showError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Schiedsrichter existiert bereits oder Fehler beim Speichern")
        }

        if (showSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Schiedsrichter erfolgreich erfasst!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Datenbankverbindungsstatus: $dbConnectionStatus")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Schiedsrichter löschen") },
            text = { Text("Möchten Sie wirklich alle Einträge zum Schiedsrichter ${refereeToDelete?.name} löschen?") },
            confirmButton = {
                Button(onClick = {
                    refereeToDelete?.let { referee ->
                        FirestoreHelper.deleteRefereeAndObservations(referee.id, // Verwende referee.id
                            onSuccess = {
                                FirestoreHelper.fetchAllRefereesWithDetails(onSuccess = { list: List<Referee> ->
                                    referees = list
                                    showDialog = false
                                }, onFailure = {
                                    showError = true
                                    showDialog = false
                                })
                            },
                            onFailure = {
                                showError = true
                                showDialog = false
                            })
                    }
                }) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
