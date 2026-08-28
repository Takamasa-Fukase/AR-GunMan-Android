package com.takamasafukase.ar_gunman_android.repositoryMock

import com.ar_gunman_android.domain.repositoryInterfaces.TutorialRepositoryInterface

class TutorialRepositoryMock : TutorialRepositoryInterface {
    private var isCompleted = false

    override fun getTutorialCompletedFlag(): Boolean {
        return isCompleted
    }

    override fun updateTutorialCompletedFlag(isCompleted: Boolean) {
        this.isCompleted = isCompleted
    }
}