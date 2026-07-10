package fyi.dayle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fyi.dayle.data.DayleDatabase
import fyi.dayle.data.MissionRepository
import fyi.dayle.data.MissionService
import fyi.dayle.ui.MissionScreen
import fyi.dayle.ui.MissionUiState
import fyi.dayle.ui.theme.DayleTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: MissionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = MissionRepository(
            database = DayleDatabase(applicationContext),
            service = MissionService(BuildConfig.API_BASE_URL)
        )
        enableEdgeToEdge()
        setContent {
            DayleTheme {
                MissionScreen(
                    loadMission = { loadMission() },
                    onToggleCompleted = { toggleCompleted() })
            }
        }
    }

    private fun loadMission(): MissionUiState {
        return try {
            val existing = repository.getTodayMission()
            val mission = existing ?: repository.fetchAndStoreTodayMission()
            MissionUiState.Loaded(mission.text, mission.completed)
        } catch (e: Exception) {
            MissionUiState.Error(e.message ?: "Error desconocido")
        }
    }

    private fun toggleCompleted(): Boolean {
        val mission = repository.getTodayMission() ?: return false
        val newCompleted = !mission.completed
        repository.setCompleted(mission, newCompleted)
        return newCompleted
    }
}
