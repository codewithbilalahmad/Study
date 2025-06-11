package com.muhammad.study.presentation.screens.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammad.study.presentation.components.AnimatedBorder
import com.muhammad.study.presentation.components.DeleteDialog
import com.muhammad.study.presentation.components.SubjectListBottomSheet
import com.muhammad.study.presentation.components.studySessionList
import com.muhammad.study.presentation.viewModel.SessionViewModel
import com.muhammad.study.utils.Constants.ACTION_SERVICE_CANCEL
import com.muhammad.study.utils.Constants.ACTION_SERVICE_START
import com.muhammad.study.utils.Constants.ACTION_SERVICE_STOP
import com.muhammad.study.utils.SnackbarEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.time.DurationUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(onBackClick: () -> Unit, timerService: StudySessionTimerService) {
    val viewModel = koinInject<SessionViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarEvent = viewModel.snackbarEventFlow
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val subjects = state.subjects
    val currentTimerState by timerService.currentTimerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                SnackbarEvent.NavigateUp -> {
                    onBackClick()
                }

                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }
    LaunchedEffect(subjects) {
        val subjectId = timerService.subjectId.value
        viewModel.onEvent(
            SessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedToSubject = subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }
    SubjectListBottomSheet(
        sheetState = sheetState,
        showBottomSheet = showBottomSheet,
        subjects = state.subjects,
        onDismiss = {
            showBottomSheet = false
        }, onSubjectClick = { subject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) showBottomSheet = false
            }
            viewModel.onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        })
    DeleteDialog(
        showDialog = showDeleteDialog,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? " +
                "This action can not be undone.",
        onDismiss = {
            showDeleteDialog = false
        }, onConfirmClick = {
            viewModel.onEvent(SessionEvent.DeleteSession)
            showDeleteDialog = false
        })
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        SessionTopBar(onBackClick = onBackClick)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimerSection(
                        modifier = Modifier
                            .size(250.dp),
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds
                    )
                }
            }
            item {
                RelatedToSubject(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = state.relatedToSubject ?: "Select your Subject",
                    selectSubjectClick = { showBottomSheet = true },
                    seconds = seconds
                )
            }
            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        if (state.subjectId != null && state.relatedToSubject != null) {
                            val action =
                                if (currentTimerState == TimerState.STARTED) ACTION_SERVICE_STOP else ACTION_SERVICE_START
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = action
                            )
                            timerService.subjectId.value = state.subjectId
                        } else {
                            viewModel.onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                    }, cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_STOP
                        )
                    }, finishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                        viewModel.onEvent(SessionEvent.SaveSession(duration))
                    }, timerState = currentTimerState, seconds = seconds
                )
            }
            studySessionList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = state.sessions,
                onDeleteClick = { session ->
                    showDeleteDialog = true
                    viewModel.onEvent(SessionEvent.OnDeleteSession(session))
                })
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SessionTopBar(onBackClick: () -> Unit) {
    TopAppBar(title = {
        Text(text = "Study Sessions", style = MaterialTheme.typography.headlineSmall)
    }, navigationIcon = {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
    })
}

@Composable
fun TimerSection(modifier: Modifier = Modifier, hours: String, minutes: String, seconds: String) {
    AnimatedBorder(modifier = modifier, shape = CircleShape, content = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = hours,
                    label = hours,
                    transitionSpec = { timerTextAnimation() }) { hours ->
                    Text(text = "$hours:", style = MaterialTheme.typography.headlineLarge)
                }
                AnimatedContent(
                    targetState = minutes,
                    label = minutes,
                    transitionSpec = { timerTextAnimation() }) { minutes ->
                    Text(text = "$minutes:", style = MaterialTheme.typography.headlineLarge)
                }
                AnimatedContent(targetState = seconds, label = seconds, transitionSpec = {
                    timerTextAnimation()
                }) { seconds ->
                    Text(text = seconds, style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    })
}

@Composable
fun RelatedToSubject(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectClick: () -> Unit,
    seconds: String,
) {
    Column(modifier = modifier) {
        Text(text = "Related to subject", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = relatedToSubject, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = selectSubjectClick, enabled = seconds == "00") {
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    }
}

@Composable
fun ButtonsSection(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = cancelButtonClick,
            enabled = seconds != "00"
        ) {
            Text(text = "Cancel", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Button(
            onClick = startButtonClick, colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = when (timerState) {
                    TimerState.IDLE -> "Start"
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                }
            )
        }
        Button(
            onClick = finishButtonClick,
            enabled = seconds != "00"
        ) {
            Text(text = "Finish", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeOut(animationSpec = tween(duration))
}