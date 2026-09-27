package edu.virginia.cs4720.jbz2et

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class DetailViewModel : ViewModel() {
    private var loadedId: String? = null

    var name by mutableStateOf("")
        private set
    var dueDate by mutableStateOf<LocalDate?>(null)
        private set
    var done by mutableStateOf(false)
        private set
    var completedDate by mutableStateOf<LocalDate?>(null)
        private set
    var showDatePicker by mutableStateOf(false)
        private set

    fun start(id: String) {
        if (loadedId != null) return
        val item = BucketRepository.items.first { it.id == id }
        loadedId = id
        name = item.name
        dueDate = item.dueDate
        done = item.done
        completedDate = item.completedDate
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun openDatePicker() {
        showDatePicker = true
    }

    fun closeDatePicker() {
        showDatePicker = false
    }

    fun pickDate(date: LocalDate) {
        dueDate = date
    }

    fun toggleDone() {
        done = !done
        completedDate = if (done) LocalDate.now() else null
    }

    fun canSave() = name.isNotBlank() && dueDate != null

    fun save() {
        val id = loadedId ?: return
        val date = dueDate ?: return
        val original = BucketRepository.items.first { it.id == id }
        BucketRepository.update(
            original.copy(
                name = name.trim(),
                dueDate = date,
                done = done,
                completedDate = completedDate
            )
        )
    }
}