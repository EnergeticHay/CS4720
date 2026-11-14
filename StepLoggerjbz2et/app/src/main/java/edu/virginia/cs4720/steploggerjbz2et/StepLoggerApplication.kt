package edu.virginia.cs4720.steploggerjbz2et

import android.app.Application
import androidx.room.Room

class StepLoggerApplication : Application() {
    val database: StepDatabase by lazy {
        Room.databaseBuilder(this, StepDatabase::class.java, "steps.db").build()
    }
    val repository: StepRepository by lazy { StepRepository(database.stepDao()) }
}