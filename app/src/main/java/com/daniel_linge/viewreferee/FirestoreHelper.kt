package com.daniel_linge.viewreferee

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
                    onSuccess(referees)
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
                                onSuccess(referees)
                            }
                        }, onFailure = {
                            processedCount++

                            if (processedCount == documents.size) {
                                onSuccess(referees)
                            }
                        })
                    } else {
                        processedCount++

                        if (processedCount == documents.size) {
                            onSuccess(referees)
                        }
                    }
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    private fun fetchObservationCountForReferee(referee: Referee, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        db.collection("observations")
            .whereEqualTo("refereeId", referee.id)
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                onSuccess(count)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun fetchRefereeByName(name: String, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {
        db.collection("referees")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                onSuccess(!result.isEmpty)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun saveReferee(referee: Referee, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("referees")
            .document(referee.id)
            .set(referee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
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
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure() }
            }
            .addOnFailureListener {
                onFailure()
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
        db.collection("observations")
            .document(observation.refereeId)
            .set(observation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}
