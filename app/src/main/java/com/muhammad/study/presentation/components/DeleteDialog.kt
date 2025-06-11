package com.muhammad.study.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun DeleteDialog(
    showDialog: Boolean,
    title: String,
    bodyText: String,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss, title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }, text = {
            Text(text = bodyText, style = MaterialTheme.typography.bodyMedium)
        }, confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = "Delete", style = MaterialTheme.typography.bodyLarge)
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", style = MaterialTheme.typography.bodyLarge)
            }
        })
    }
}