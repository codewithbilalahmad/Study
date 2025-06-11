package com.muhammad.study.presentation.screens.subject

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import androidx.navigation.NavHostController
import com.muhammad.study.presentation.components.*
import com.muhammad.study.presentation.navigation.Destinations
import com.muhammad.study.presentation.viewModel.*
import com.muhammad.study.utils.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    viewModel: SubjectViewModel,
    onEvent: (SubjectEvent) -> Unit,
    onBack: () -> Unit,navHostController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarEvent = viewModel.snackbarFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFabExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    var showEditSubjectDialog by remember { mutableStateOf(false) }
    var showDeleteSubjectDialog by remember { mutableStateOf(false) }
    var showDeleteSessionDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                SnackbarEvent.NavigateUp -> onBack()
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }
    LaunchedEffect(state.studiedHours, state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)
    }
    AddSubjectDialog(
        showDialog = showEditSubjectDialog,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        selectedColors = state.subjectCardColors,
        onSubjectNameChange = { newValue ->
            onEvent(SubjectEvent.OnSubjectNameChange(newValue))
        }, onGoalHourChange = { newValue ->
            onEvent(SubjectEvent.OnSubjectStudyHoursChange(newValue))
        }, onDismiss = {
            showEditSubjectDialog = false
        }, onConfirmClick = {
            onEvent(SubjectEvent.UpdateSubject)
            showEditSubjectDialog = false
        }, onColorChange = { colors ->
            onEvent(SubjectEvent.OnSubjectCardColorChange(colors))
        })
    DeleteDialog(
        showDialog = showDeleteSubjectDialog,
        title = "Delete Subject?",
        bodyText = "Are you sure, you want to delete this subject? All related " +
                "tasks and study sessions will be permanently removed. This action can not be undone",
        onDismiss = { showDeleteSubjectDialog = false },
        onConfirmClick = {
            onEvent(SubjectEvent.DeleteSubject)
            showDeleteSubjectDialog = false
        }
    )
    DeleteDialog(
        showDialog = showDeleteSessionDialog,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced " +
                "by this session time. This action can not be undone.", onDismiss = {
            showDeleteSessionDialog = false
        }, onConfirmClick = {
            onEvent(SubjectEvent.DeleteSession)
            showDeleteSessionDialog = false
        }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectTopBar(
                title = state.subjectName,
                onBack = onBack,
                onDelete = { showDeleteSubjectDialog = true },
                onEditClick = { showEditSubjectDialog = true },
                scrollBehavior = scrollBehavior
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                val taskId = 0L
                val subjectId = state.currentSubjectId
                navHostController.navigate("${Destinations.TaskScreen.route}/$taskId/$subjectId")
            }, icon = {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }, text = {
                Text(text = "Add Task", style = MaterialTheme.typography.bodyLarge)
            }, expanded = isFabExpanded)
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), state = listState
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = state.studiedHours.toString(),
                    goalHour = state.goalStudyHours,
                    progress = state.progress
                )
            }
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n " +
                        "Click the + button to add new task.",
                tasks = state.upcomingTasks,
                onCheckBoxClick = { task ->
                    onEvent(SubjectEvent.OnTaskIsCompleteChange(task))
                }, onTaskClick = {

                }
            )
            item {
                Spacer(Modifier.height(20.dp))
            }
            tasksList(
                sectionTitle = "COMPLETED TASKS",
                emptyListText = "You don't have any completed tasks.\n " +
                        "Click the check box on completion of task.",
                tasks = state.completedTasks,
                onCheckBoxClick = { task ->
                    onEvent(SubjectEvent.OnTaskIsCompleteChange(task))
                }, onTaskClick = {

                })
            item {
                Spacer(Modifier.height(20.dp))
            }
            studySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = state.recentSessions,
                onDeleteClick = { session ->
                    showDeleteSessionDialog = true
                    onEvent(SubjectEvent.OnDeleteSession(session))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectTopBar(
    title: String,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEditClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeTopAppBar(scrollBehavior = scrollBehavior, navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
    }, title = {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }, actions = {
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
        }
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
        }
    })
}

@SuppressLint("DefaultLocale")
@Composable
fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHour: String,
    progress: Float,
) {
    val percentageProgress = remember(progress) {
        (progress * 100).toFloat().coerceIn(0f, 100f)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHour ?: "0.0"
        )
        Spacer(Modifier.width(12.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = String.format("%.2f%%", percentageProgress), style = MaterialTheme.typography.bodyLarge)
        }
    }
}