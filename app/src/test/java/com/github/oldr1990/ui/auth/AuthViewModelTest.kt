package com.github.oldr1990.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.oldr1990.FakeAuthRepository
import com.github.oldr1990.MainCoroutineRule
import com.github.oldr1990.TestConstants.CORRECT_EMAIL
import com.github.oldr1990.TestConstants.CORRECT_PASSWORD
import com.github.oldr1990.TestConstants.WRONG_EMAIL
import com.github.oldr1990.model.UserEntries
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.robolectric.annotation.Config

@RunWith(JUnit4::class)
@Config(manifest = Config.NONE)
@FixMethodOrder(MethodSorters.DEFAULT)
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(FakeAuthRepository())
    }

    @After
    fun cleanUp() {
        ViewModelStore().clear()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun a_loginWrongEmailFail() = runBlockingTest {

        runBlocking { viewModel.login(UserEntries(CORRECT_EMAIL, CORRECT_PASSWORD)) }
        val event = runBlocking { viewModel.authEvent.value }
        val answer = when (event) {
            AuthViewModel.AuthEvent.Empty -> false
            is AuthViewModel.AuthEvent.Error -> {
               println(event.message)
                true
            }
            is AuthViewModel.AuthEvent.Success -> false
            AuthViewModel.AuthEvent.WrongEmail -> true
            AuthViewModel.AuthEvent.WrongPassword -> false
        }
        assertThat(answer).isTrue()
    }


}