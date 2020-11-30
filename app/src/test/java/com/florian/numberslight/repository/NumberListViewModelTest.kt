package com.florian.numberslight.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.florian.numberslight.model.NumberSummary
import com.florian.numberslight.repository.NumberRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(MockitoJUnitRunner::class)
class NumberListViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mockitoRule = MockitoJUnit.rule()

    private val result = mock<List<NumberSummary>> ()
    @Mock
    private lateinit var repository: NumberRepository
    private val viewModel by lazy { NumberListViewModel(repository) }

    @Before
    fun initTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    fun getListWhenNoError() {
        val latch = CountDownLatch(1)
        repository.stub {
            onBlocking { getList() } doReturn result
        }

        viewModel.networkError.observe(lifeCycleOwner) {
            if( it != null) {
                latch.countDown()
            }
        }

        runBlocking {
            viewModel.refreshList()
            latch.await(100, TimeUnit.MICROSECONDS)
            assertEquals(1, latch.count)
        }
    }
    fun getListWhenNetworkError() {}
    fun getListWhenNoData() {}

    private val lifeCycleOwner by lazy{
        val owner: LifecycleOwner = mock()
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        whenever(owner.lifecycle).thenReturn(lifecycle)
        owner
    }
}