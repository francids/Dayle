import "../models/mission.dart";
import "../utils/date_util.dart";
import "database_service.dart";
import "mission_service.dart";

class MissionRepository {
  static const _recentLimit = 10;

  final DayleDatabase _database;
  final MissionService _service;

  MissionRepository(this._database, this._service);

  Future<Mission?> getTodayMission() async {
    return _database.getMissionForDate(DateUtil.today());
  }

  Future<void> setCompleted(Mission mission, bool completed) async {
    await _database.setCompleted(mission.date, completed);
  }

  Future<Mission> fetchAndStoreTodayMission() async {
    final recent = await _database.getRecentMissions(_recentLimit);
    final previous = recent.map((m) => m.text).toList();
    final text = await _service.fetchMission(previous);
    return _database.insertMission(DateUtil.today(), text);
  }
}
