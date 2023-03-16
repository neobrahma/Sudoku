package com.lconsulting.sudoku.ui.main

import com.lconsulting.sudoku.data.SudokuData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SudokuViewModelTest : Spek({

    val viewModel by memoized { SudokuViewModel() }
    val sudoku by memoized { SudokuData() }

    describe("getIndexSquareInGrid") {

        listOf(
            1 to 1,
            12 to 3,
            40 to 4,
            44 to 5,
            60 to 0
        ).forEach {
            it("index in grid for this index ${it.first} in sudoku is ${it.second}") {
                val result = viewModel.getIndexSquareInGrid(it.first)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getIndexGrid") {
        var index = 70
        var resultExpected = 8
        it("with index $index and should return indexGrid to $resultExpected") {
            val result = viewModel.getIndexGrid(index)
            assertEquals(resultExpected, result)
        }
    }

    describe("getIndex") {
        var idGrid = 4
        var idSquare = 8
        var resultExpected = 50
        it("with idGrid $idGrid & idSquare $idSquare, should return index $resultExpected\"") {
            val result = viewModel.getIndex(idGrid, idSquare)
            assertEquals(resultExpected, result)
        }
    }

    describe("getStartIndexGrid") {
        var index = 70
        var resultExpected = 60
        it("with index $index, should return startIndexGrid $resultExpected\"") {
            val result = viewModel.getStartIndexGrid(index)
            assertEquals(resultExpected, result)
        }
    }

    describe("getStartIndexColumn") {
        var index = 70
        var resultExpected = 7
        it("with index $index, should return startIndexColumn $resultExpected\"") {
            val result = viewModel.getStartIndexColumn(index)
            assertEquals(resultExpected, result)
        }
    }

    describe("getStartIndexRow") {
        var index = 70
        var resultExpected = 63
        it("with index $index, should return startIndexRow $resultExpected\"") {
            val result = viewModel.getStartIndexRow(index)
            assertEquals(resultExpected, result)
        }
    }

    describe("getIndexInGrid") {
        listOf(
            (0 to 0) to 0,
            (30 to 3) to 39,
            (60 to 5) to 71
        ).forEach {
            it("with start Index ${it.first.first} & position ${it.first.second} to, should return index : ${it.second}") {
                val result = viewModel.getIndexInGrid(it.first.first, it.first.second)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getIndexInRow") {
        listOf(
            (0 to 0) to 0,
            (36 to 3) to 39,
            (63 to 5) to 68
        ).forEach {
            it("with start Index to  ${it.first.first} & position ${it.first.second}, index is ${it.second}") {
                val result = viewModel.getIndexInRow(it.first.first, it.first.second)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getIndexInColumn") {
        listOf(
            (0 to 0) to 0,
            (4 to 3) to 31,
            (8 to 5) to 53
        ).forEach {
            it("with start Index to  ${it.first.first} & position ${it.first.second} to, index is ${it.second}") {
                val result = viewModel.getIndexInColumn(it.first.first, it.first.second)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getStartIndexRowByPosition") {
        listOf(
            0 to 0,
            4 to 36,
            6 to 54
        ).forEach {
            it("with position to  ${it.first}, start index row is ${it.second}") {
                val result = viewModel.getStartIndexRowByPosition(it.first)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getStartIndexGridByPosition") {
        listOf(
            0 to 0,
            4 to 30,
            6 to 54
        ).forEach {
            it("with position to  ${it.first}, start index grid  is ${it.second}") {
                val result = viewModel.getStartIndexGridByPosition(it.first)
                assertEquals(it.second, result)
            }
        }
    }

    describe("getStartIndexColumnByPosition") {
        listOf(
            0 to 0,
            4 to 4,
            6 to 6
        ).forEach {
            it("with position to  ${it.first}, start index column is ${it.second}") {
                val result = viewModel.getStartIndexColumnByPosition(it.first)
                assertEquals(it.second, result)
            }
        }
    }

    describe("removeValuePair with modification") {
        listOf(
            0 to 1
        ).forEach { pair ->
            beforeEachTest {
                sudoku[pair.second].possibility.removeIf { it != 1 && it != 2 }
                viewModel.sudokuData = sudoku
            }
            it("remove value from indexPair ${pair.second} on index ${pair.first} and should return true") {
                val result = viewModel.removeValuePair(pair.first, pair.second)
                assertEquals(true, result)
                assertEquals(7, viewModel.sudokuData[pair.first].possibility.size)
                assertEquals(2, viewModel.sudokuData[pair.second].possibility.size)
            }
        }
    }

    describe("removeValuePair with no modification") {
        listOf(
            0 to 1
        ).forEach { pair ->
            beforeEachTest {
                sudoku[pair.first].possibility.removeIf { it == 1 || it == 2 }
                sudoku[pair.second].possibility.removeIf { it != 1 && it != 2 }
                viewModel.sudokuData = sudoku
            }
            it("remove value from indexPair ${pair.second} on index ${pair.first} and should return false") {
                val result = viewModel.removeValuePair(pair.first, pair.second)
                assertEquals(false, result)
                assertEquals(7, viewModel.sudokuData[pair.first].possibility.size)
                assertEquals(2, viewModel.sudokuData[pair.second].possibility.size)
            }
        }
    }

    describe("containsOnlyPair with 2 identical sets") {
        listOf(
            0 to 1
        ).forEach { pair ->
            beforeEachTest {
                sudoku[pair.first].possibility.removeIf { it != 1 && it != 2 }
                sudoku[pair.second].possibility.removeIf { it != 1 && it != 2 }
                viewModel.sudokuData = sudoku
            }
            it("from indexPair ${pair.second} on index ${pair.first} and should return true") {
                val result = viewModel.containsOnlyPair(
                    viewModel.sudokuData[pair.first].possibility,
                    viewModel.sudokuData[pair.second].possibility
                )
                assertEquals(true, result)
            }
        }
    }

    describe("containsOnlyPair with 2 different sets") {
        listOf(
            0 to 1
        ).forEach { pair ->
            beforeEachTest {
                sudoku[pair.first].possibility.removeIf { it != 1 && it != 2 }
                sudoku[pair.second].possibility.removeIf { it != 2 && it != 4 }
                viewModel.sudokuData = sudoku
            }
            it("from indexPair ${pair.second} on index ${pair.first} and should return false") {
                val result = viewModel.containsOnlyPair(
                    viewModel.sudokuData[pair.first].possibility,
                    viewModel.sudokuData[pair.second].possibility
                )

                assertEquals(false, result)
            }
        }
    }

    describe("containsOnlyPair with a set who contains more 2 values") {
        listOf(
            0 to 1
        ).forEach { pair ->
            beforeEachTest {
                sudoku[pair.second].possibility.removeIf { it != 2 && it != 4 }
                println("${sudoku[pair.second].possibility}")
                viewModel.sudokuData = sudoku
            }
            it("from indexPair ${pair.second} on index ${pair.first} and should return false") {
                val result = viewModel.containsOnlyPair(
                    viewModel.sudokuData[pair.first].possibility,
                    viewModel.sudokuData[pair.second].possibility
                )
                assertNotEquals(2, sudoku[pair.first].possibility.size)
                assertEquals(2, sudoku[pair.second].possibility.size)
                assertEquals(false, result)
            }
        }
    }
})