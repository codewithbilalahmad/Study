package com.muhammad.study.presentation.screens.session

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.*
import androidx.compose.runtime.*
import androidx.core.app.*
import com.muhammad.study.StudyApplication
import com.muhammad.study.utils.Constants.ACTION_SERVICE_CANCEL
import com.muhammad.study.utils.Constants.ACTION_SERVICE_START
import com.muhammad.study.utils.Constants.ACTION_SERVICE_STOP
import com.muhammad.study.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.muhammad.study.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.muhammad.study.utils.Constants.NOTIFICATION_ID
import com.muhammad.study.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import kotlin.concurrent.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

class StudySessionTimerService() : Service() {
    private val context = StudyApplication.INSTANCE
     private var notificationManager: NotificationManager = provideNotificationManager(context)
     private var notificationBuilder: NotificationCompat.Builder =  provideNotificationBuilder(context)
    private val binder = StudySessionTimerBinder()
    private lateinit var timer: Timer
    var duration: Duration = Duration.ZERO
        private set
    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set
    var currentTimerState = MutableStateFlow(TimerState.IDLE)
        private set
    var subjectId = mutableStateOf<Long?>(null)
    override fun onBind(intent: Intent?) = binder
    inner class StudySessionTimerBinder : Binder() {
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {action ->
            when(action){
                ACTION_SERVICE_START ->{
                    startForegroundService()
                    startTimer { hours, minutes, seconds ->
                        updateNotification(hours = hours,minutes =  minutes, seconds =  seconds)
                    }
                }
                ACTION_SERVICE_STOP ->{
                    stopTimer()
                    cancelTimer()
                }
                ACTION_SERVICE_CANCEL ->{
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startForegroundService(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    @SuppressLint("NewApi")
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Session Study Timer Notification"
        notificationManager.createNotificationChannel(channel)
    }
    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }

    private fun startTimer(onTick: (h: String, m: String, s: String) -> Unit) {
        currentTimerState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentTimerState.value = TimerState.STOPPED
    }

    private fun cancelTimer() {
        duration = Duration.ZERO
        updateTimeUnits()
        currentTimerState.value = TimerState.IDLE
    }

    private fun updateTimeUnits() {
        val totalSeconds = duration.inWholeSeconds
        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds  % 60).toInt()
        this@StudySessionTimerService.hours.value = hours.pad()
        this@StudySessionTimerService.minutes.value = minutes.pad()
        this@StudySessionTimerService.seconds.value = seconds.pad()
    }
}

enum class TimerState {
    IDLE, STARTED, STOPPED
}