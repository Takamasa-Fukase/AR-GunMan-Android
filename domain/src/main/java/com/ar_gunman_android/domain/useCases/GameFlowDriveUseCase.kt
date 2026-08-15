package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.game.GameFlowStatus
import com.ar_gunman_android.domain.entities.game.GameTimeCount
import com.ar_gunman_android.domain.repositoryInterfaces.TutorialRepositoryInterface
import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

interface GameFlowDriveUseCaseInterface {
    val statusStream: SharedFlow<GameFlowStatus>
    suspend fun start()
    suspend fun pauseTimer()
    suspend fun resolveBlocked()
}

class GameFlowDriveUseCase(
    private val tutorialRepository: TutorialRepositoryInterface,
    private val gameStore: GameStoreInterface,
    private val scope: CoroutineScope,
) : GameFlowDriveUseCaseInterface {
    override val statusStream: SharedFlow<GameFlowStatus> get() = _statusStream.asSharedFlow()

    private val _statusStream = MutableSharedFlow<GameFlowStatus>()
    private var timerJob: Job? = null

    override suspend fun start() {
        if (gameStore.gameFlow.value.status != GameFlowStatus.FlowNotStarted) return
        updateAndHandleNextStatus(nextStatus = GameFlowStatus.CheckingTutorialCompletedStatus)
    }

    override suspend fun pauseTimer() {
        if (!gameStore.gameFlow.value.status.isTimerRunning) return
        disposeTimer()
        updateAndHandleNextStatus(nextStatus = GameFlowStatus.Blocked(GameFlowStatus.BlockedReason.TIMER_PAUSED))
    }

    override suspend fun resolveBlocked() {
        val currentStatus = gameStore.gameFlow.value.status as? GameFlowStatus.Blocked ?: return
        when (currentStatus.reason) {
            GameFlowStatus.BlockedReason.TUTORIAL_NOT_COMPLETED -> {
                tutorialRepository.updateTutorialCompletedFlag(isCompleted = true)
                updateAndHandleNextStatus(nextStatus = GameFlowStatus.WaitingForTimerStart)
            }

            GameFlowStatus.BlockedReason.TIMER_PAUSED -> {
                updateAndHandleNextStatus(nextStatus = GameFlowStatus.TimerResumedAndWaitingForTimerEnd)
            }
        }
    }

    private suspend fun updateAndHandleNextStatus(nextStatus: GameFlowStatus) {
        gameStore.updateGameFlow { gameFlow ->
            gameFlow.drive(nextStatus = nextStatus)
        }
        handleUpdatedStatus(status = nextStatus)
        _statusStream.emit(nextStatus)
    }

    private suspend fun handleUpdatedStatus(status: GameFlowStatus) {
        when (status) {
            GameFlowStatus.CheckingTutorialCompletedStatus -> {
                val isTutorialCompleted = tutorialRepository.getTutorialCompletedFlag()
                if (isTutorialCompleted) {
                    updateAndHandleNextStatus(nextStatus = GameFlowStatus.WaitingForTimerStart)

                } else {
                    updateAndHandleNextStatus(nextStatus = GameFlowStatus.Blocked(reason = GameFlowStatus.BlockedReason.TUTORIAL_NOT_COMPLETED))
                }
            }

            GameFlowStatus.WaitingForTimerStart -> {
                scope.launch {
                    // 1.5秒待機
                    delay(timeMillis = 1500)
                    updateAndHandleNextStatus(nextStatus = GameFlowStatus.TimerStartedAndWaitingForTimerEnd)
                }
            }

            GameFlowStatus.TimerStartedAndWaitingForTimerEnd, GameFlowStatus.TimerResumedAndWaitingForTimerEnd -> {
                timerJob = scope.launch {
                    while (coroutineContext.isActive) {
                        if (gameStore.timeCount.value.isTimeUp) {
                            updateAndHandleNextStatus(nextStatus = GameFlowStatus.TimerEndedAndWaitingForFlowEnd)
                            disposeTimer()
                            break
                        }

                        // タイマー更新間隔の秒数分待機
                        delay(timeMillis = GameTimeCount.updateIntervalMillisec.toLong())
                        gameStore.updateTimeCount { timeCount ->
                            timeCount.decrement()
                        }
                    }
                }
            }

            GameFlowStatus.TimerEndedAndWaitingForFlowEnd -> {
                scope.launch {
                    // 1.5秒待機
                    delay(timeMillis = 1500)
                    updateAndHandleNextStatus(nextStatus = GameFlowStatus.FlowEnded)
                }
            }

            GameFlowStatus.FlowNotStarted, GameFlowStatus.FlowEnded, is GameFlowStatus.Blocked -> Unit
        }
    }

    private fun disposeTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}