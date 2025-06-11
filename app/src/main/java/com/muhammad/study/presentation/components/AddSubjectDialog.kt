package com.muhammad.study.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.muhammad.study.domain.model.Subject
import com.muhammad.study.utils.rippleClickable

@Composable
fun AddSubjectDialog(
    showDialog: Boolean,
    title: String = "Add/Update Subject",
    selectedColors: List<Color>,
    subjectName: String,
    goalHours: String,
    onColorChange: (List<Color>) -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onGoalHourChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    var subjectNameError by remember { mutableStateOf<String?>(null) }
    var goalHoursError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(subjectName) {
        if (subjectName.isNotBlank()) {
            subjectNameError = when {
                subjectName.isBlank() -> "Please enter subject name."
                subjectName.length < 2 -> "Subject name is too short."
                subjectName.length > 20 -> "Subject name is too long"
                else -> null
            }
        }
    }
    LaunchedEffect(goalHours) {
        if (goalHours.isNotBlank()) {
            goalHoursError = when {
                goalHours.isBlank() -> "Please enter study hours."
                goalHours.toFloatOrNull() == null -> "Invalid number."
                goalHours.toFloat() < 1f -> "Please set at least 1 hour."
                goalHours.toFloat() > 1000f -> "Please set a maximum of 1000 hours"
                else -> null
            }
        }
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss, title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }, text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Subject.subjectCardColors.forEach { colors ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (colors == selectedColors) Color.Black else Color.Transparent,
                                    CircleShape
                                )
                                .background(brush = Brush.verticalGradient(colors), CircleShape)
                                .rippleClickable{ onColorChange(colors) })
                    }
                }
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = onSubjectNameChange,
                    label = {
                        Text(text = "Subject Name", style = MaterialTheme.typography.bodyLarge)
                    },
                    singleLine = true,
                    isError = subjectNameError != null && subjectName.isNotBlank(),
                    supportingText = {
                        Text(
                            text = subjectNameError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    })
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = goalHours,
                    onValueChange = onGoalHourChange,
                    label = {
                        Text(text = "Goal Study Hours", style = MaterialTheme.typography.bodyLarge)
                    },
                    singleLine = true,
                    isError = goalHoursError != null && goalHours.isNotBlank(),
                    supportingText = {
                        Text(
                            text = goalHoursError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    })
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", style = MaterialTheme.typography.bodyLarge)
            }
        }, confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled = subjectNameError == null && goalHoursError == null
            ) {
                Text(text = "Save", style = MaterialTheme.typography.bodyLarge)
            }
        })
    }
}