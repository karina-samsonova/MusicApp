package com.example.auth.data.repository

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockAuthResult: AuthResult
    private lateinit var repository: AuthRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockAuth = mock()
        mockAuthResult = mock()
        repository = AuthRepositoryImpl(mockAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials returns success`() = runTest {
        whenever(mockAuth.signInWithEmailAndPassword("test@mail.com", "123456"))
            .thenReturn(TaskCompletionSource<AuthResult>().apply {
                setResult(mockAuthResult)
            }.task)

        val result = repository.login("test@mail.com", "123456")

        assertTrue(result.isSuccess)
        verify(mockAuth).signInWithEmailAndPassword("test@mail.com", "123456")
    }

    @Test
    fun `login with invalid credentials returns failure`() = runTest {
        val exception = Exception("Invalid credentials")
        whenever(mockAuth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(TaskCompletionSource<AuthResult>().apply {
                setException(exception)
            }.task)

        val result = repository.login("wrong@mail.com", "123456")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register new user successfully`() = runTest {
        whenever(mockAuth.createUserWithEmailAndPassword("new@mail.com", "123456"))
            .thenReturn(TaskCompletionSource<AuthResult>().apply {
                setResult(mockAuthResult)
            }.task)

        val result = repository.register("new@mail.com", "123456")

        assertTrue(result.isSuccess)
        verify(mockAuth).createUserWithEmailAndPassword("new@mail.com", "123456")
    }

    @Test
    fun `isUserLoggedIn returns true when user exists`() {
        whenever(mockAuth.currentUser).thenReturn(mock())

        assertTrue(repository.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn returns false when no user`() {
        whenever(mockAuth.currentUser).thenReturn(null)
        assertTrue(!repository.isUserLoggedIn())
    }

    @Test
    fun `logout calls signOut`() {
        repository.logout()
        verify(mockAuth).signOut()
    }
}