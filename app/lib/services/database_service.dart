import "package:sqflite/sqflite.dart";

import "../models/mission.dart";

class DayleDatabase {
  static const _dbName = "dayle.db";
  static const _dbVersion = 1;
  static Database? _db;

  Future<Database> get database async {
    _db ??= await _initDb();
    return _db!;
  }

  Future<Database> _initDb() async {
    final dbPath = await getDatabasesPath();
    final path = "$dbPath/$_dbName";
    return openDatabase(path, version: _dbVersion, onCreate: _onCreate);
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute("""
      CREATE TABLE missions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        date TEXT NOT NULL UNIQUE,
        text TEXT NOT NULL,
        completed INTEGER NOT NULL DEFAULT 0
      )
    """);
  }

  Future<Mission?> getMissionForDate(String date) async {
    final db = await database;
    final results = await db.query(
      "missions",
      where: "date = ?",
      whereArgs: [date],
    );
    if (results.isEmpty) return null;
    return Mission.fromMap(results.first);
  }

  Future<Mission> insertMission(String date, String text) async {
    final db = await database;
    final id = await db.insert("missions", {
      "date": date,
      "text": text,
      "completed": 0,
    }, conflictAlgorithm: ConflictAlgorithm.replace);
    return Mission(id: id, date: date, text: text, completed: false);
  }

  Future<void> setCompleted(String date, bool completed) async {
    final db = await database;
    await db.update(
      "missions",
      {"completed": completed ? 1 : 0},
      where: "date = ?",
      whereArgs: [date],
    );
  }

  Future<List<Mission>> getRecentMissions(int limit) async {
    final db = await database;
    final results = await db.query(
      "missions",
      where: "completed = 1",
      orderBy: "date DESC",
      limit: limit,
    );
    return results.map((map) => Mission.fromMap(map)).toList();
  }
}
