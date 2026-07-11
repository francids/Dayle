import "package:flutter/material.dart";

import "screens/mission_screen.dart";
import "services/database_service.dart";
import "services/mission_repository.dart";
import "services/mission_service.dart";
import "theme/app_theme.dart";

void main() {
  final repository = MissionRepository(DayleDatabase(), MissionService());

  runApp(DayleApp(repository: repository));
}

class DayleApp extends StatelessWidget {
  final MissionRepository repository;

  const DayleApp({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "Dayle",
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      themeMode: ThemeMode.system,
      home: MissionScreen(repository: repository),
    );
  }
}
