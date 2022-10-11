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

    // test on getting reminders
    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        // we check if we have error then we return result error and handle it in a toast or snackbar else we try to find that reminder by id
        // if it exists then we return result sucsess with that reminder if not then we return result error reminder doesnt exist
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

    // test on clearing reminders
    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    // function to set the error by true or false
    fun setShouldReturnError(b: Boolean) {

        shouldReturnError = b
    }


}