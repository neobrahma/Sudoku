package com.lconsulting.sudoku.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SudokuViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutor = InstantTaskExecutorRule()

    lateinit var viewModel: SudokuViewModel

    @Before
    fun setUp() {
        viewModel = SudokuViewModel()
    }

    @Test
    fun test_getStartIndexColumnBy9() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexColumnBy9", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 5
        assertEquals(5, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getStartIndexGridBy9() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexGridBy9", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 5
        assertEquals(33, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getStartIndexRowBy9() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexRowBy9", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 5
        assertEquals(45, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getIndexForRow() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndexForRow", Int::class.java, Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(2)
        parameters[0] = 36
        parameters[1] = 5
        assertEquals(41, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getIndexForColumn() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndexForColumn", Int::class.java, Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(2)
        parameters[0] = 5
        parameters[1] = 5
        assertEquals(50, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getIndexForGrid() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndexForGrid", Int::class.java, Int::class.java)
        method.isAccessible = true

        val parameters = arrayOfNulls<Any>(2)
        parameters[0] = 30
        parameters[1] = 0
        assertEquals(30, method.invoke(viewModel, *parameters))

        parameters[0] = 30
        parameters[1] = 2
        assertEquals(32, method.invoke(viewModel, *parameters))

        parameters[0] = 30
        parameters[1] = 5
        assertEquals(41, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getStartIndexRow() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexRow", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 70
        assertEquals(63, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getStartIndexColumn() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexColumn", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 70
        assertEquals(7, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getStartIndexGrid() {
        val method = viewModel.javaClass.getDeclaredMethod("getStartIndexGrid", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 70
        assertEquals(60, method.invoke(viewModel, *parameters))
    }
    
    @Test
    fun test_getIndex() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndex", Int::class.java, Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(2)
        parameters[0] = 1
        parameters[1] = 4
        assertEquals(13, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getIndexGrid() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndexGrid", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 70
        assertEquals(8, method.invoke(viewModel, *parameters))
    }

    @Test
    fun test_getIndexSquareInGrid() {
        val method = viewModel.javaClass.getDeclaredMethod("getIndexSquareInGrid", Int::class.java)
        method.isAccessible = true
        val parameters = arrayOfNulls<Any>(1)
        parameters[0] = 61
        assertEquals(1, method.invoke(viewModel, *parameters))
    }

}
