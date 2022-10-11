package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(maxSdk = Build.VERSION_CODES.P)
class RemindersListViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // setting up needed resourses
    @Before
    fun setup() {
        stopKoin()

        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun loadReminder_showsLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        // show loading at start
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // hide loading after getting response
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


    @Test
    fun withReminders_listNotEmpty() = runBlockingTest {

        // checking that reminder list returns empty when it is firstly loaded before adding reminders
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty()).isTrue()
        // saving a reminder
        fakeDataSource.saveReminder(
            ReminderDTO(
                "test1",
                "desc",
                "eg",
                34.34,
                34.34
            )
        )
        // checking that list is not empty after adding an item to list
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty()).isFalse()
    }


    @Test
    fun remindersUnavailable_showsError() = runBlockingTest {
        // setting error to true
        fakeDataSource.setShouldReturnError(true)

        // testing that loading reminders after setting error to true returns error and shows a snackbar
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isNotEmpty()
    }
}