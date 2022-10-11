package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    // initializing the db before we start using it
    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    // closing db after test is ended
    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveReminder() = runBlocking {
        // saving a complete reminder
        val reminder = ReminderDTO("test 1", "desc", "loca", 34.12, 34.12)
        repository.saveReminder(reminder)


        // getting that reminder by the same id we passed
        val result = repository.getReminder(reminder.id) as? Result.Success
// asserting that the returning result is sucsess since we didnt pass shouldReturnError is true
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success
// asserting that the returned reminder has same title,desc,location,lat,lng as the reminder we passed

        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }

    @Test
    fun deleteReminders_EmptyList() = runBlocking {
        // saving a complete reminder
        val reminder = ReminderDTO("test 1", "desc", "loca", 34.12, 34.12)
        repository.saveReminder(reminder)
// deleting all reminders
        repository.deleteAllReminders()
// getting reminders
        val result = repository.getReminders()
// asserting that it doesnt return error but return result success and the returning list is empty
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data, `is`(emptyList()))
    }
}