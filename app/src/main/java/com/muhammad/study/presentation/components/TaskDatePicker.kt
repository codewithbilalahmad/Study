package com.muhammad.study.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state: DatePickerState,
    showPicker: Boolean,
    onDismiss: () -> Unit,
    onConfirmClicked: (Long?) -> Unit,
) {
    if (showPicker) {
        DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = state.selectedDateMillis
                onConfirmClicked(selectedDateMillis)
                onDismiss()
            }) {
                Text(text = "OK", style = MaterialTheme.typography.bodyLarge)
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", style = MaterialTheme.typography.bodyLarge)
            }
        }, content = {
            DatePicker(state = state)
        })
    }
}