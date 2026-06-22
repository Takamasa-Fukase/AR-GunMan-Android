package com.example.domain.repositoryInterfaces

interface TutorialRepositoryInterface {
    fun getTutorialCompletedFlag(): Boolean
    fun updateTutorialCompletedFlag(isCompleted: Boolean)
}