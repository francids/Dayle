import "package:flutter/material.dart";

import "../services/mission_repository.dart";

sealed class MissionUiState {}

class MissionLoading extends MissionUiState {}

class MissionError extends MissionUiState {
  final String message;
  MissionError(this.message);
}

class MissionLoaded extends MissionUiState {
  final String text;
  final bool completed;
  MissionLoaded(this.text, this.completed);
}

class MissionScreen extends StatefulWidget {
  final MissionRepository repository;

  const MissionScreen({super.key, required this.repository});

  @override
  State<MissionScreen> createState() => _MissionScreenState();
}

class _MissionScreenState extends State<MissionScreen> {
  MissionUiState _state = MissionLoading();

  @override
  void initState() {
    super.initState();
    _loadMission();
  }

  Future<void> _loadMission() async {
    try {
      final existing = await widget.repository.getTodayMission();
      final mission =
          existing ?? await widget.repository.fetchAndStoreTodayMission();
      setState(() {
        _state = MissionLoaded(mission.text, mission.completed);
      });
    } catch (e) {
      setState(() {
        _state = MissionError(e.toString());
      });
    }
  }

  Future<void> _toggleCompleted() async {
    final current = _state;
    if (current is! MissionLoaded) return;

    final newCompleted = !current.completed;
    final todayMission = await widget.repository.getTodayMission();
    if (todayMission != null) {
      await widget.repository.setCompleted(todayMission, newCompleted);
      setState(() {
        _state = MissionLoaded(current.text, newCompleted);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: switch (_state) {
            MissionLoading() => const SizedBox(
              width: 48,
              height: 48,
              child: CircularProgressIndicator(),
            ),
            MissionError(:final message) => Text(
              "No se pudo cargar la misión.\n$message",
              style: theme.textTheme.bodyLarge?.copyWith(
                color: theme.colorScheme.error,
              ),
              textAlign: TextAlign.center,
            ),
            MissionLoaded(:final text, :final completed) => GestureDetector(
              onTap: _toggleCompleted,
              child: Text(
                text,
                style: theme.textTheme.headlineMedium?.copyWith(
                  color: completed
                      ? theme.colorScheme.onSurfaceVariant
                      : theme.colorScheme.onSurface,
                  decoration: completed ? TextDecoration.lineThrough : null,
                ),
                textAlign: TextAlign.center,
              ),
            ),
          },
        ),
      ),
    );
  }
}
