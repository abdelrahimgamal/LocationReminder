package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setup() {
        stopKoin()

        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun saveReminder_allDataSet() {
        val reminderItem = ReminderDataItem("test", "test", "test", 34.34, 34.34)
        mainCoroutineRule.pauseDispatcher()

        viewModel.validateAndSaveReminder(reminderItem)
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))


        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun saveReminder_emptyTitle() {
        val reminderItem = ReminderDataItem("", "test", "test", 34.34, 34.34)

        viewModel.validateAndSaveReminder(reminderItem)
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isNotNull()
    }

    @Test
    fun saveReminder_emptyLoca() {
        val reminderItem = ReminderDataItem("test", "test", null, 34.34, 34.34)

        viewModel.validateAndSaveReminder(reminderItem)
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isNotNull()
    }


    @Test
    fun saveReminder_sucsess_navigateUp() {
        val reminderItem = ReminderDataItem("test", "test", "test", 34.34, 34.34)

        viewModel.validateAndSaveReminder(reminderItem)
        assertThat(viewModel.navigationCommand.getOrAwaitValue()).isEqualTo(NavigationCommand.Back)
    }


}