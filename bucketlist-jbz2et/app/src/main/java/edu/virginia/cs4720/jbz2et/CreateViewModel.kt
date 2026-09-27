package edu.virginia.cs4720.jbz2et

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class CreateViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var dueDate by mutableStateOf<LocalDate?>(null)
        private set
    var showDatePicker by mutableStateOf(false)
        private set

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

    fun canSave() = name.isNotBlank() && dueDate != null

    fun save() {
        val date = dueDate ?: return
        BucketRepository.add(BucketItem(name = name.trim(), dueDate = date))
    }
}