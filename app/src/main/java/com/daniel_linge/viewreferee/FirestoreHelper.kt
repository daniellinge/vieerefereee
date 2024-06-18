package com.daniel_linge.viewreferee

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object FirestoreHelper {
    fun fetchAllRefereesWithDetails(onSuccess: (List<Referee>) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("referees")
            .get()
            .addOnSuccessListener { result ->
                val referees = result.toObjects(Referee::class.java).toMutableList()

                if (referees.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        onSuccess(referees)
                    }
                    return@addOnSuccessListener
                }

                var processedCount = 0

                referees.forEach { referee ->
                    fetchLastObservationDateForReferee(referee.id, onSuccess = { lastObservationDate ->
                        referee.lastObservationDate = lastObservationDate // Datum setzen

                        fetchObservationCountForReferee(referee, onSuccess = { count ->
                            referee.observationCount = count
                            processedCount++
                            if (processedCount == referees.size) {
                                Handler(Looper.getMainLooper()).post {
                                    onSuccess(referees)
                                }
                            }
                        }, onFailure = {
                            Log.e("FirestoreHelper", "Error fetching observation count")
                            processedCount++
                            if (processedCount == referees.size) {
                                Handler(Looper.getMainLooper()).post {
                                    onSuccess(referees)
                                }
                            }
                        })
                    }, onFailure = {
                        Log.e("FirestoreHelper", "Error fetching last observation date")
                        processedCount++
                        if (processedCount == referees.size) {
                            Handler(Looper.getMainLooper()).post {
                                onSuccess(referees)
                            }
                        }
                    })
                }
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Error fetching referees", it)
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }
    private fun fetchObservationCountForReferee(referee: Referee, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("observations")
            .whereEqualTo("refereeId", referee.id)
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                Handler(Looper.getMainLooper()).post {
                    onSuccess(count)
                }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }
    fun fetchRefereeByName(name: String, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("referees")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                Handler(Looper.getMainLooper()).post {
                    onSuccess(!result.isEmpty)
                }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }
    fun saveReferee(referee: Referee, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("referees")
            .document(referee.id)
            .set(referee)
            .addOnSuccessListener {
                Handler(Looper.getMainLooper()).post {
                    onSuccess()
                }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }

    fun deleteRefereeAndObservations(refereeId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        val refereeRef = db.collection("referees").document(refereeId)
        batch.delete(refereeRef)

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
                        Handler(Looper.getMainLooper()).post {
                            onSuccess()
                        }
                    }
                    .addOnFailureListener {
                        Handler(Looper.getMainLooper()).post {
                            onFailure()
                        }
                    }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }

    fun fetchObservations(onSuccess: (List<Observation>) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
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

    private fun fetchLastObservationDateForReferee(refereeId: String, onSuccess: (Date?) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("referees")
            .document(refereeId)
            .get()
            .addOnSuccessListener { document ->
                val lastObservationDate = document.getDate("lastObservationDate")
                Handler(Looper.getMainLooper()).post {
                    onSuccess(lastObservationDate)
                }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    onFailure()
                }
            }
    }


    fun saveObservation(observation: Observation, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (observation.refereeId.isBlank()) {
            onFailure()
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("observations")
            .add(observation)
            .addOnSuccessListener { _ ->
                db.collection("referees")
                    .document(observation.refereeId)
                    .update("lastObservationDate", observation.timestamp)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure() }
            }
            .addOnFailureListener { onFailure() }
        }
}
