package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // instanciate db
    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        dao = database.reminderDao()
    }

    // finish db
    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveReminder_sucsess() = runBlockingTest {

        // saving a complete reminder
        val reminder = ReminderDTO("test 1", "desc", "loca", 34.12, 34.12)
        database.reminderDao().saveReminder(reminder)


        // testing on getting the reminder by id from db
        val result = database.reminderDao().getReminderById(reminder.id)

// checking that the returned reminder is not null and it has same id,title,description,location,lat and lng
        assertThat(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))

    }


    @Test
    fun retriveAllFromDB() = runBlockingTest {
        // saving 2 reminders
        val reminder = ReminderDTO("test 1", "desc1 ", "loca1 ", 34.12, 34.12)
        val reminder2 = ReminderDTO("test 2", "desc2 ", "loca2 ", 34.12, 34.12)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        // testing on getting the reminders from database and asserting that the list is not null

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, `is`(notNullValue()))
    }

    @Test
    fun insertReminders_deleteAllReminders() = runBlockingTest {
        // saving 2 reminders
        val reminder = ReminderDTO("test 1", "desc1 ", "loca1 ", 34.12, 34.12)
        val reminder2 = ReminderDTO("test 2", "desc2 ", "loca2 ", 34.12, 34.12)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        // deleting the 2 reminders
        database.reminderDao().deleteAllReminders()

        // getting the reminder list
        val remindersList = database.reminderDao().getReminders()
        // asserting that the list is empty since we cleared the list
        assertThat(remindersList, `is`(emptyList()))
    }


}