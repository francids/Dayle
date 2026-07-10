package fyi.dayle.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface MissionUiState {
    data object Loading : MissionUiState
    data class Error(val message: String) : MissionUiState
    data class Loaded(val text: String, val completed: Boolean) : MissionUiState
}

@Composable
fun MissionScreen(
    loadMission: () -> MissionUiState,
    onToggleCompleted: () -> Boolean,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf<MissionUiState>(MissionUiState.Loading) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            state = withContext(Dispatchers.IO) { loadMission() }
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val current = state) {
                is MissionUiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(48.dp))

                is MissionUiState.Error -> Text(
                    text = "No se pudo cargar la misión.\n${current.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )

                is MissionUiState.Loaded -> MissionText(
                    text = current.text, completed = current.completed, onClick = {
                        val newCompleted = onToggleCompleted()
                        state = current.copy(completed = newCompleted)
                    })
            }
        }
    }
}

@Composable
private fun MissionText(
    text: String, completed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onBackground,
        textDecoration = if (completed) TextDecoration.LineThrough else null,
        style = MaterialTheme.typography.headlineMedium
    )
}
