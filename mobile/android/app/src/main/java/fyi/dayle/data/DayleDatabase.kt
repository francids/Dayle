package fyi.dayle.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DayleDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE missions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL UNIQUE,
                text TEXT NOT NULL,
                completed INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS missions")
        onCreate(db)
    }

    fun getMissionForDate(date: String): Mission? {
        val cursor = readableDatabase.query(
            "missions",
            arrayOf("id", "date", "text", "completed"),
            "date = ?",
            arrayOf(date),
            null,
            null,
            null
        )
        cursor.use {
            if (!it.moveToFirst()) return null
            return Mission(
                id = it.getLong(it.getColumnIndexOrThrow("id")),
                date = it.getString(it.getColumnIndexOrThrow("date")),
                text = it.getString(it.getColumnIndexOrThrow("text")),
                completed = it.getInt(it.getColumnIndexOrThrow("completed")) == 1
            )
        }
    }

    fun insertMission(date: String, text: String): Mission {
        val values = ContentValues().apply {
            put("date", date)
            put("text", text)
            put("completed", 0)
        }
        val id = writableDatabase.insertWithOnConflict(
            "missions", null, values, SQLiteDatabase.CONFLICT_REPLACE
        )
        return Mission(id = id, date = date, text = text, completed = false)
    }

    fun setCompleted(date: String, completed: Boolean) {
        val values = ContentValues().apply { put("completed", if (completed) 1 else 0) }
        writableDatabase.update("missions", values, "date = ?", arrayOf(date))
    }

    fun getRecentMissions(limit: Int): List<Mission> {
        val result = mutableListOf<Mission>()
        val cursor = readableDatabase.query(
            "missions",
            arrayOf("id", "date", "text", "completed"),
            "completed = 1",
            null,
            null,
            null,
            "date DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    Mission(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        date = it.getString(it.getColumnIndexOrThrow("date")),
                        text = it.getString(it.getColumnIndexOrThrow("text")),
                        completed = it.getInt(it.getColumnIndexOrThrow("completed")) == 1
                    )
                )
            }
        }
        return result
    }

    companion object {
        private const val DB_NAME = "dayle.db"
        private const val DB_VERSION = 1
    }
}
