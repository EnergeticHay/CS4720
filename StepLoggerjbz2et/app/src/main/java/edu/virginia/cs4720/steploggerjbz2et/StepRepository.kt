package edu.virginia.cs4720.steploggerjbz2et

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class StepRepository(private val dao: StepDao) {
    fun observeHistory(): Flow<List<DailySteps>> = dao.observeHistory()

    suspend fun stepsOn(date: LocalDate): Int = dao.getDay(date.toString())?.steps ?: 0

    suspend fun saveDay(date: LocalDate, steps: Int) = dao.saveDay(DailySteps(date.toString(), steps))

    suspend fun lastCounterValue(): Long? = dao.getCounter()?.lastValue

    suspend fun saveCounterValue(value: Long?) {
        if (value == null) dao.clearCounter() else dao.saveCounter(CounterState(lastValue = value))
    }

    suspend fun deleteOlderThan(cutoff: LocalDate) = dao.deleteDaysBefore(cutoff.toString())

    suspend fun clearAll() {
        dao.deleteAllDays()
        dao.clearCounter()
    }

    companion object {
        const val RETENTION_DAYS = 30L
    }
}