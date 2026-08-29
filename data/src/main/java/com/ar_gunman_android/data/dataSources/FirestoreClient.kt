package com.ar_gunman_android.data.dataSources

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface FirestoreClientInterface {
    suspend fun <R : Any> getItems(collectionPath: String, responseType: Class<R>): List<R>
    suspend fun <R : Any> addItem(collectionPath: String, request: R)
}

class FirestoreClient : FirestoreClientInterface {
    private val db = Firebase.firestore
    override suspend fun <R : Any> getItems(collectionPath: String, responseType: Class<R>): List<R> {
        return try {
             db.collection(collectionPath)
                .get()
                .await()
                .toObjects(responseType)
        } catch (error: Exception) {
            Log.d("Android", "ログAndroid: FirestoreClient getItems error: $error")
            throw error
        }
    }

    override suspend fun <R : Any> addItem(collectionPath: String, request: R) {
        try {
            db.collection(collectionPath)
                .add(request)
                .await()
        } catch (error: Exception) {
            Log.d("Android", "ログAndroid: FirestoreClient addItem error: $error")
            throw error
        }
    }
}