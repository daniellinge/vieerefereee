package com.daniel_linge.viewreferee

import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()
    fun fetchAllRefereesWithDetails(onSuccess: (List<Referee>) -> Unit, onFailure: () -> Unit) {
        db.collection("referees")
            .get()
            .addOnSuccessListener { result ->
                val referees = mutableListOf<Referee>()
                val documents = result.documents

                if (documents.isEmpty()) {
                    // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                    Handler(Looper.getMainLooper()).post {
                        onSuccess(referees)
                    }
                    return@addOnSuccessListener
                }

                var processedCount = 0

                documents.forEach { document ->
                    val referee = document.toObject(Referee::class.java)
                    if (referee != null) {
                        fetchObservationCountForReferee(referee, onSuccess = { count ->
                            referee.observationCount = count
                            referees.add(referee)
                            processedCount++

                            if (processedCount == documents.size) {
                                // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                                Handler(Looper.getMainLooper()).post {
                                    onSuccess(referees)
                                }
                            }
                        }, onFailure = {
                            processedCount++

                            if (processedCount == documents.size) {
                                // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                                Handler(Looper.getMainLooper()).post {
                                    onSuccess(referees)
                                }
                            }
                        })
                    } else {
                        processedCount++

                        if (processedCount == documents.size) {
                            // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                            Handler(Looper.getMainLooper()).post {
                                onSuccess(referees)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Verschiebe den Aufruf von onFailure auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }


//

    private fun fetchObservationCountForReferee(referee: Referee, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        db.collection("observations")
            .whereEqualTo("refereeId", referee.id)
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onSuccess(count)
                }
            }
            .addOnFailureListener {
                // Verschiebe den Aufruf von onFailure auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }
    fun fetchRefereeByName(name: String, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {
        db.collection("referees")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onSuccess(!result.isEmpty)
                }
            }
            .addOnFailureListener {
                // Verschiebe den Aufruf von onFailure auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }

    fun saveReferee(referee: Referee, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("referees")
            .document(referee.id)
            .set(referee)
            .addOnSuccessListener {
                // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onSuccess()
                }
            }
            .addOnFailureListener {
                // Verschiebe den Aufruf von onFailure auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }

    fun deleteRefereeAndObservations(refereeId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val batch = db.batch()

        // Delete referee
        val refereeRef = db.collection("referees").document(refereeId)
        batch.delete(refereeRef)

        // Delete observations
        db.collection("observations")
            .whereEqualTo("refereeId", refereeId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val observationRef = document.reference
                    batch.delete(observationRef)
                }
                batch.commit()
                    .addOnSuccessListener {
                        // Verschiebe den Aufruf von onSuccess auf den Hauptthread
                        Handler(Looper.getMainLooper()).post {
                            onSuccess()
                        }
                    }
                    .addOnFailureListener {
                        // Verschiebe den Aufruf von onFailure auf den Hauptthread
                        Handler(Looper.getMainLooper()).post {
                            onFailure()
                        }
                    }
            }
            .addOnFailureListener {
                // Verschiebe den Aufruf von onFailure auf den Hauptthread
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }

    fun fetchObservations(onSuccess: (List<Observation>) -> Unit, onFailure: () -> Unit) {
        db.collection("observations")
            .get()
            .addOnSuccessListener { result ->
                val observations = result.mapNotNull { document ->
                    document.toObject(Observation::class.java)
                }
                onSuccess(observations)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun saveObservation(observation: Observation, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (observation.refereeId.isBlank()) {
            onFailure() // Oder eine Fehlermeldung anzeigen
            return
        }

        db.collection("observations")
            .document(observation.refereeId)
            .set(observation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}
