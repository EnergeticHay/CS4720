package edu.virginia.cs4720.steploggerjbz2et

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_steps")
data class DailySteps(
    @PrimaryKey val date: String,
    val steps: Int
)

@Entity(tableName = "counter_state")
data class CounterState(
    @PrimaryKey val id: Int = 0,
    val lastValue: Long
)

@Dao
interface StepDao {
    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun observeHistory(): Flow<List<DailySteps>>

    @Query("SELECT * FROM daily_steps WHERE date = :date")
    suspend fun getDay(date: String): DailySteps?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDay(day: DailySteps)

    @Query("DELETE FROM daily_steps WHERE date < :cutoff")
    suspend fun deleteDaysBefore(cutoff: String)

    @Query("DELETE FROM daily_steps")
    suspend fun deleteAllDays()

    @Query("SELECT * FROM counter_state WHERE id = 0")
    suspend fun getCounter(): CounterState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCounter(state: CounterState)

    @Query("DELETE FROM counter_state")
    suspend fun clearCounter()
}

@Database(entities = [DailySteps::class, CounterState::class], version = 1, exportSchema = false)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
}