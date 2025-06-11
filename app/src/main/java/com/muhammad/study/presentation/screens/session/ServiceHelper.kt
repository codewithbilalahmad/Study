package com.muhammad.study.presentation.screens.session

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muhammad.study.MainActivity
import com.muhammad.study.presentation.navigation.Destinations
import com.muhammad.study.utils.Constants

object ServiceHelper {
    fun clickPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("destination", Destinations.SessionScreen.route)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            Constants.CLICK_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}