package com.flagquest.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.flagquest.app.domain.model.UserStats
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStatsRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val uid get() = auth.currentUser?.uid ?: error("Not logged in")

    suspend fun getStats(): UserStats {
        val doc = firestore.collection("users").document(uid).get().await()
        return if (doc.exists()) {
            UserStats(
                userId = uid,
                displayName = auth.currentUser?.email ?: "Joueur",
                totalQuizzes = doc.getLong("totalQuizzes")?.toInt() ?: 0,
                totalCorrect = doc.getLong("totalCorrect")?.toInt() ?: 0,
                bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                currentStreak = doc.getLong("currentStreak")?.toInt() ?: 0,
                totalScore = doc.getLong("totalScore")?.toInt() ?: 0,
                level = doc.getLong("level")?.toInt() ?: 1,
                xp = doc.getLong("xp")?.toInt() ?: 0
            )
        } else {
            UserStats(userId = uid, displayName = auth.currentUser?.email ?: "Joueur")
        }
    }

    suspend fun saveQuizResult(correct: Int, total: Int) {
        val ref = firestore.collection("users").document(uid)
        firestore.runTransaction { transaction ->
            val doc = transaction.get(ref)
            val prevQuizzes = doc.getLong("totalQuizzes")?.toInt() ?: 0
            val prevCorrect = doc.getLong("totalCorrect")?.toInt() ?: 0
            val prevScore = doc.getLong("totalScore")?.toInt() ?: 0
            val prevXp = doc.getLong("xp")?.toInt() ?: 0
            val prevLevel = doc.getLong("level")?.toInt() ?: 1
            val prevStreak = doc.getLong("currentStreak")?.toInt() ?: 0
            val prevBest = doc.getLong("bestStreak")?.toInt() ?: 0

            val score = correct * 10
            val newStreak = if (correct == total) prevStreak + 1 else 0
            val newXp = prevXp + score
            val newLevel = (newXp / 100) + 1

            transaction.set(ref, mapOf(
                "totalQuizzes" to prevQuizzes + 1,
                "totalCorrect" to prevCorrect + correct,
                "totalScore" to prevScore + score,
                "currentStreak" to newStreak,
                "bestStreak" to maxOf(prevBest, newStreak),
                "xp" to newXp,
                "level" to newLevel
            ))
        }.await()
    }
}
