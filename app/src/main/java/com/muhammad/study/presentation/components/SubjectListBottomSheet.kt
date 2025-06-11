package com.muhammad.study.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.muhammad.study.domain.model.Subject
import com.muhammad.study.utils.rippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    subjects: List<Subject>,
    onSubjectClick: (Subject) -> Unit, onDismiss: () -> Unit,
) {
    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, dragHandle = {}) {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                if(subjects.isNotEmpty()){
                    items(subjects) { subject ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .rippleClickable { onSubjectClick(subject) }
                                .padding(16.dp)
                        ) {
                            Text(text = subject.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else{
                    item{
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = "No Subjects founds!", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}