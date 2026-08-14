package com.ar_gunman_android.domain.entities.game

data class GameFlow(
    val status: GameFlowStatus = GameFlowStatus.FlowNotStarted
) {
    internal fun drive(nextStatus: GameFlowStatus): GameFlow {
        return this.copy(status = nextStatus)
    }
}

sealed interface GameFlowStatus {
    object FlowNotStarted : GameFlowStatus
    object CheckingTutorialCompletedStatus : GameFlowStatus
    object WaitingForTimerStart : GameFlowStatus
    object TimerStartedAndWaitingForTimerEnd : GameFlowStatus
    object TimerResumedAndWaitingForTimerEnd : GameFlowStatus
    object TimerEndedAndWaitingForFlowEnd : GameFlowStatus
    object FlowEnded : GameFlowStatus
    data class Blocked(val reason: BlockedReason) : GameFlowStatus

    enum class BlockedReason {
        TUTORIAL_NOT_COMPLETED,
        TIMER_PAUSED;
    }

    val isTimerRunning: Boolean
        get() = when (this) {
            TimerStartedAndWaitingForTimerEnd,
            TimerResumedAndWaitingForTimerEnd -> true
            else -> false
        }
}