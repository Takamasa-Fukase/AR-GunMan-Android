package com.takamasafukase.ar_gunman_android.factories

import android.content.Context
import com.ar_gunman_android.data.dataSources.FirestoreClient
import com.ar_gunman_android.data.dataSources.FirestoreClientInterface
import com.ar_gunman_android.data.repositories.RankingRepository
import com.ar_gunman_android.data.repositories.stubs.TutorialRepositoryStub
import com.ar_gunman_android.device.arShootingEngine.ARShootingEngineHandler
import com.ar_gunman_android.device.arShootingEngine.ARShootingEngineHandlerInterface
import com.ar_gunman_android.device.cameraPermission.CameraPermissionHandler
import com.ar_gunman_android.device.cameraPermission.CameraPermissionHandlerInterface
import com.ar_gunman_android.device.motionSensor.MotionSensorHandler
import com.ar_gunman_android.device.motionSensor.MotionSensorHandlerInterface
import com.ar_gunman_android.device.sound.SoundPlayer
import com.ar_gunman_android.device.sound.SoundPlayerInterfaces
import com.ar_gunman_android.domain.repositoryInterfaces.RankingRepositoryInterface
import com.ar_gunman_android.domain.repositoryInterfaces.TutorialRepositoryInterface
import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import com.ar_gunman_android.domain.useCases.GameFlowDriveUseCase
import com.ar_gunman_android.domain.useCases.GameFlowDriveUseCaseInterface
import com.ar_gunman_android.domain.useCases.ReloadingMotionCountUpdateUseCase
import com.ar_gunman_android.domain.useCases.ReloadingMotionCountUpdateUseCaseInterface
import com.ar_gunman_android.domain.useCases.ScoreAddUseCase
import com.ar_gunman_android.domain.useCases.ScoreAddUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponChangeUseCase
import com.ar_gunman_android.domain.useCases.WeaponChangeUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponControlMotionDetectUseCase
import com.ar_gunman_android.domain.useCases.WeaponControlMotionDetectUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponFireUseCase
import com.ar_gunman_android.domain.useCases.WeaponFireUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponReloadUseCase
import com.ar_gunman_android.domain.useCases.WeaponReloadUseCaseInterface
import com.takamasafukase.ar_gunman_android.stores.GameStore
import com.takamasafukase.ar_gunman_android.stores.RankingStore
import com.takamasafukase.ar_gunman_android.stores.WeaponStore
import kotlinx.coroutines.CoroutineScope

class Factory(
    private val context: Context
) {
    // MARK: Devices
    fun createARShootingEngineHandler(): ARShootingEngineHandlerInterface {
        return ARShootingEngineHandler()
    }

    fun createCameraPermissionHandler(): CameraPermissionHandlerInterface {
        return CameraPermissionHandler(context = context)
    }

    fun createMotionSensorHandler(): MotionSensorHandlerInterface {
        return MotionSensorHandler(context = context)
    }

    fun createSoundPlayer(): SoundPlayerInterfaces {
        return SoundPlayer(context = context)
    }

    // MARK: DataSources
    fun createFirestoreClient(): FirestoreClientInterface {
        return FirestoreClient()
    }

    // MARK: Repositories
    fun createTutorialRepository(): TutorialRepositoryInterface {
        // TODO: 本物を作って差し替える
        return TutorialRepositoryStub()
    }

    fun createRankingRepository(): RankingRepositoryInterface {
        return RankingRepository(
            firestoreClient = createFirestoreClient()
        )
    }

    // MARK: Stores
    fun createRankingStore(): RankingStoreInterface {
        return RankingStore
    }

    fun createWeaponStore(): WeaponStoreInterface {
        return WeaponStore
    }

    fun createGameStore(): GameStoreInterface {
        return GameStore
    }

    // MARK: UseCases
    // TODO: Ranking系のUseCase2つ

    fun createWeaponFireUseCase(
        weaponReloadUseCase: WeaponReloadUseCaseInterface
    ): WeaponFireUseCaseInterface {
        return WeaponFireUseCase(
            weaponStore = createWeaponStore(),
            weaponReloadUseCase = weaponReloadUseCase
        )
    }

    fun createWeaponReloadUseCase(scope: CoroutineScope): WeaponReloadUseCaseInterface {
        return WeaponReloadUseCase(
            weaponStore = createWeaponStore(),
            scope = scope
        )
    }

    fun createWeaponChangeUseCase(
        weaponReloadUseCase: WeaponReloadUseCaseInterface
    ): WeaponChangeUseCaseInterface {
        return WeaponChangeUseCase(
            weaponStore = createWeaponStore(),
            weaponReloadUseCase = weaponReloadUseCase
        )
    }

    fun createGameFlowDriveUseCase(scope: CoroutineScope): GameFlowDriveUseCaseInterface {
        return GameFlowDriveUseCase(
            tutorialRepository = createTutorialRepository(),
            gameStore = createGameStore(),
            scope = scope
        )
    }

    fun createScoreAddUseCase(): ScoreAddUseCaseInterface {
        return ScoreAddUseCase(gameStore = createGameStore())
    }

    fun createReloadingMotionCountUpdateUseCase(): ReloadingMotionCountUpdateUseCaseInterface {
        return ReloadingMotionCountUpdateUseCase(gameStore = createGameStore())
    }

    fun createWeaponControlMotionDetectUseCase(): WeaponControlMotionDetectUseCaseInterface {
        return WeaponControlMotionDetectUseCase()
    }
}