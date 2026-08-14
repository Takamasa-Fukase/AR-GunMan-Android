package com.ar_gunman_android.domain.repositoryInterfaces

interface TutorialRepositoryInterface {
    fun getTutorialCompletedFlag(): Boolean
    fun updateTutorialCompletedFlag(isCompleted: Boolean)
}