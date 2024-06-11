package com.daniel_linge.viewreferee

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FirestoreHelperTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var mockCollection: CollectionReference
    private lateinit var mockDocument: DocumentReference

    @Before
    fun setUp() {
        // Mock Firestore und die CollectionReference
        db = Mockito.mock(FirebaseFirestore::class.java)
        mockCollection = Mockito.mock(CollectionReference::class.java)
        mockDocument = Mockito.mock(DocumentReference::class.java)

        Mockito.`when`(db.collection(Mockito.anyString())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.document(Mockito.anyString())).thenReturn(mockDocument)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidDocumentReference() {
        // Manuelles Überprüfen der Dokumentreferenz
        validateDocumentReference("observations")
    }

    @Test
    fun testValidDocumentReference() {
        // Manuelles Überprüfen der Dokumentreferenz
        validateDocumentReference("observations/validReference")
    }

    private fun validateDocumentReference(path: String) {
        val segments = path.split("/")
        if (segments.size % 2 != 0) {
            throw IllegalArgumentException("Document references must have an even number of segments, but $path has ${segments.size}")
        }
        // Simulieren des tatsächlichen Verhaltens bei Firebase
        db.collection("observations").document(path)
    }
}

