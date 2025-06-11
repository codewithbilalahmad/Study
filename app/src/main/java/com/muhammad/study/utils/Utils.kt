package com.muhammad.study.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.muhammad.study.R
import com.muhammad.study.presentation.navigation.Destinations
import com.muhammad.study.presentation.screens.session.ServiceHelper
import com.muhammad.study.presentation.theme.Green
import com.muhammad.study.presentation.theme.Orange
import com.muhammad.study.presentation.theme.Red
import com.muhammad.study.utils.Constants.NOTIFICATION_CHANNEL_ID
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: MEDIUM
    }
}

@Composable
fun Modifier.rippleClickable(onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return this.clickable(indication = null, interactionSource = interaction) {
        onClick()
    }
}

@SuppressLint("NewApi")
fun Long?.changeMillisToDateString(): String {
    val date: LocalDate = this?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Long.toHours(): Float {
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}

sealed class SnackbarEvent {
    data class ShowSnackbar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short,
    ) : SnackbarEvent()

    data object NavigateUp : SnackbarEvent()
}

fun Int.pad(): String {
    return this.toString().padStart(length = 2, padChar = '0')
}

fun provideNotificationBuilder(context: Context): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Study Session").setContentText("00:00:00")
        .setSmallIcon(R.drawable.ic_lamp).setOngoing(true)
        .setContentIntent(ServiceHelper.clickPendingIntent(context))
}

@Composable
fun HandleIntentNavigation(navHostController: NavHostController, intent: Intent?) {
    LaunchedEffect(Unit) {
        val destination = intent?.getStringExtra("destination")
        if (destination != null) {
            navHostController.navigate(destination) {
                popUpTo(Destinations.HomeScreen.route) { inclusive = false }
            }
        }
    }
}

fun provideNotificationManager(context: Context): NotificationManager {
    return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

fun checkPermissionGranted(permission: String, context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}