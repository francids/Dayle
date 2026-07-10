package fyi.dayle.data

class MissionRepository(
    private val database: DayleDatabase, private val service: MissionService
) {

    fun getTodayMission(): Mission? = database.getMissionForDate(DateUtil.today())

    fun setCompleted(mission: Mission, completed: Boolean) {
        database.setCompleted(mission.date, completed)
    }

    fun fetchAndStoreTodayMission(): Mission {
        val previous = database.getRecentMissions(RECENT_LIMIT).map { it.text }
        val text = service.fetchMission(previous)
        return database.insertMission(DateUtil.today(), text)
    }

    companion object {
        private const val RECENT_LIMIT = 10
    }
}
