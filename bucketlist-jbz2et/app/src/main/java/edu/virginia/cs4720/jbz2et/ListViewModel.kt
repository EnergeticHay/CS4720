package edu.virginia.cs4720.jbz2et

import androidx.lifecycle.ViewModel
import java.time.LocalDate

class ListViewModel : ViewModel() {

    fun items() = BucketRepository.items

    fun toggle(id: String) {
        val item = BucketRepository.items.first { it.id == id }
        BucketRepository.update(
            if (item.done) item.copy(done = false, completedDate = null)
            else item.copy(done = true, completedDate = LocalDate.now())
        )
    }
}