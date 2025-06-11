package com.muhammad.study.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.muhammad.study.R
import com.muhammad.study.domain.model.Subject
import com.muhammad.study.presentation.components.AddSubjectDialog
import com.muhammad.study.presentation.components.CountCard
import com.muhammad.study.presentation.components.DeleteDialog
import com.muhammad.study.presentation.components.SubjectCard
import com.muhammad.study.presentation.components.studySessionList
import com.muhammad.study.presentation.components.tasksList
import com.muhammad.study.presentation.navigation.Destinations
import com.muhammad.study.presentation.viewModel.HomeViewModel
import com.muhammad.study.utils.SnackbarEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
    val viewModel = koinInject<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val showAddSubjectDialog = state.showAddSubjectDialog
    var showDeleteSessionDialog by remember { mutableStateOf(false) }
    val snackbarEvent = viewModel.snackEventFlow
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    LaunchedEffect(true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                SnackbarEvent.NavigateUp -> Unit
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }
    AddSubjectDialog(
        showDialog = showAddSubjectDialog,
        selectedColors = state.subjectCardColors,
        onSubjectNameChange = { newValue ->
            viewModel.onEvent(HomeEvent.OnSubjectNameChange(newValue))
        }, onGoalHourChange = { newValue ->
            viewModel.onEvent((HomeEvent.OnGoalStudyHoursChange(newValue)))
        }, onColorChange = { colors ->
            viewModel.onEvent(HomeEvent.OnSubjectCardColorChange(colors = colors))
        }, onDismiss = {
            viewModel.onEvent(HomeEvent.OnToggleShowAddSubjectDialog)
        }, onConfirmClick = {
            viewModel.onEvent(HomeEvent.SaveSubject)
        }, goalHours = state.goalStudyHours, subjectName = state.subjectName
    )
    DeleteDialog(
        showDialog = showDeleteSessionDialog,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced " +
                "by this session time. This action can not be undone.",
        onDismiss = {
            showDeleteSessionDialog = false
        }, onConfirmClick = {
            viewModel.onEvent(HomeEvent.DeleteSession)
            showDeleteSessionDialog = false
        })
    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Study", style = MaterialTheme.typography.headlineMedium)
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
        ) {
            item {
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString()
                )
            }
            item {
                SubjectCardSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddClick = {
                        viewModel.onEvent(HomeEvent.OnToggleShowAddSubjectDialog)
                    },
                    onSubjectCardClick = { subjectId ->
                        navHostController.navigate("${Destinations.SubjectScreen.route}/$subjectId")
                    })
            }
            item {
                Button(
                    onClick = {
                        navHostController.navigate(Destinations.SessionScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start Study Session", style = MaterialTheme.typography.bodyLarge)
                }
            }
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n " +
                        "Click the + button in subject screen to add new task.",
                tasks = tasks,
                onCheckBoxClick = { task ->
                    viewModel.onEvent(HomeEvent.OnTaskCompleteChange(task))
                }, onTaskClick = { taskId ->
                    val subjectId = 0L
                    navHostController.navigate("${Destinations.TaskScreen.route}/$taskId/$subjectId")
                })
            item {
                Spacer(Modifier.height(20.dp))
            }
            studySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = recentSessions,
                onDeleteClick = { session ->
                    viewModel.onEvent(HomeEvent.OnDeleteSession(session))
                    showDeleteSessionDialog = true
                })
        }
    }
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String,
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CountCard(
                modifier = Modifier.wrapContentSize(),
                headingText = "Subject Count",
                count = "$subjectCount"
            )
        }
        item {
            CountCard(
                modifier = Modifier.wrapContentSize(),
                headingText = "Studied Hours",
                count = studiedHours
            )
        }
        item {
            CountCard(
                modifier = Modifier.wrapContentSize(),
                headingText = "Goal Study Hours",
                count = goalHours
            )
        }
    }
}

@Composable
fun SubjectCardSection(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyListText: String = "You don't have any subjects.\n Click the + button to add new subject.",
    onAddClick: () -> Unit,
    onSubjectCardClick: (Long) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = ImageVector.vectorResource(R.drawable.ic_books),
                contentDescription = null
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { subject ->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = subject.colors.map { Color(it) },
                    onClick = { onSubjectCardClick(subject.subjectId!!) }
                )
            }
        }
    }
}