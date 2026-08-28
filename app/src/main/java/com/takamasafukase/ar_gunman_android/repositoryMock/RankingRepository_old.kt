package com.takamasafukase.ar_gunman_android.repositoryMock

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ar_gunman_android.domain.entities.ranking.Ranking

class RankingRepositoryOld {
    private val db = Firebase.firestore

    fun getRankings(
        onData: (List<Ranking>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("worldRanking")
            .orderBy("score", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                onData(
                    documents.toObjects(Ranking::class.java)
                )
            }
            .addOnFailureListener { error ->
                Log.d("Android", "ログAndroid: getRankings error: $error")
                onError(error)
            }
    }

    fun getDummyRankings(
        onData: (List<Ranking>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            onData(
                (1..150).map { Ranking(score = 98.765, user_name = "ユーザー名") }
            )
//            onError(
//                Exception("Failed to get ranking list.")
//            )
        }, 1000)
    }

    fun registerRanking(
        ranking: Ranking,
        onCompleted: () -> Unit,
        onError: (Exception) -> Unit
    ) {
       db.collection("worldRanking")
           .add(ranking)
           .addOnSuccessListener {
               onCompleted()
           }
           .addOnFailureListener { error ->
               Log.d("Android", "ログAndroid: getRankings error: $error")
               onError(error)
           }
    }

    fun registerDummyNewRanking(
        ranking: Ranking,
        onCompleted: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            onCompleted()
        }, 500)
    }
}