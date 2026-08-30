package com.ar_gunman_android.data.repositories.stubs

import com.ar_gunman_android.domain.repositoryInterfaces.TutorialRepositoryInterface

class TutorialRepositoryStub : TutorialRepositoryInterface {
    private var isCompleted = false

    override fun getTutorialCompletedFlag(): Boolean {
        return isCompleted
    }

    override fun updateTutorialCompletedFlag(isCompleted: Boolean) {
        this.isCompleted = isCompleted
    }
}