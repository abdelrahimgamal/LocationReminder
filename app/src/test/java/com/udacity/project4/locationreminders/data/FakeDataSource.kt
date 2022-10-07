package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error("Error", 404)
        } else
            Result.Success(reminders.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return when {
            shouldReturnError -> {
                Result.Error("error in returning data")
            }
            else -> {
                val reminder = reminders.find { it.id == id }
                if (reminder != null) {
                    Result.Success(reminder)
                } else {
                    Result.Error("reminder doesnt exist")
                }
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun setShouldReturnError(b: Boolean) {

        shouldReturnError = b
    }


}