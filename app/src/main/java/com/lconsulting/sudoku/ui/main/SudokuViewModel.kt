package com.lconsulting.sudoku.ui.main

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData
import com.lconsulting.sudoku.data.SudokuData
import com.lconsulting.sudoku.memento.Caretaker
import com.lconsulting.sudoku.memento.Originator


sealed class SudokuState {
    class FillSquare(
        val sudoku: Array<SquareData>, val idRes: Int, val value: Int,
        val isFirstItem: Boolean, val isLastItem: Boolean,
        val listSquareData: MutableList<SquareData>
    ) : SudokuState()

    class ModifyPossibility(
        val sudoku: Array<SquareData>
    ) : SudokuState()

    class SuccessAlgo(
        val sudoku: Array<SquareData>,
        val idRes: Int,
        val listValueSelected: Set<Int>,
        val listSquareSelectedToKeep: List<Pair<Int, Int>>,
        val listSquareSelectedToRemove: List<Pair<Int, Int>>
    ) : SudokuState()

    class RestoreState(
        val sudoku: Array<SquareData>,
        val isFirstItem: Boolean, val isLastItem: Boolean
    ) : SudokuState()

    class RefreshTouchPad(
        val listSquareData: MutableList<SquareData>
    ) : SudokuState()

    class DisplayButton(val possibility: MutableSet<Int>) : SudokuState()
    class Reset(val solution: Array<SquareData>) : SudokuState()
    class DisplayMessage(val idResString: Int) : SudokuState()
}

/**
 *
 * Sudoku 9x9
 * Grid 3x3
 * Index in Sudoku in [0, 80]
 *
 * ||00|01|02||03|04|05||06|07|08||
 * ||09|10|11||12|13|14||15|16|17||
 * ||18|19|20||21|22|23||24|25|26||
 * --------------------------------
 * ||27|28|29||30|31|32||33|34|35||
 * ||36|37|38||39|40|41||42|43|44||
 * ||45|46|47||48|49|50||51|52|53||
 * --------------------------------
 * ||54|55|56||57|58|59||60|61|62||
 * ||63|64|65||66|67|68||69|70|71||
 * ||72|73|74||75|76|77||78|79|80||
 *
 */


class SudokuViewModel : ViewModel() {

    private val caretaker: Caretaker = Caretaker()
    private val originator: Originator = Originator()

    val state = MutableLiveData<SudokuState>()

    lateinit var sudokuData: SudokuData

    private var mIdColorRes = R.color.colorText
    private var mIdMode = MODE_INSERT

    /**
     * reset sudoku to default values
     */
    fun reset() {
        sudokuData.reset()
        state.postValue(SudokuState.Reset(sudokuData.sudoku))
        caretaker.clearMemento()
        originator.clearState()
    }

    /**
     * get digits available for the selected square
     *
     * @param idGrid
     * @param idSquare
     */
    fun getDigitAvailable(idGrid: Int, idSquare: Int) {
        val pos = getIndex(idGrid, idSquare)

        val squareData = sudokuData[pos]
        state.postValue(SudokuState.DisplayButton(squareData.possibility))
    }

    /**
     * if there already is a value in the square, remove old value
     * insert value in sudoku
     *
     * @param sValue value to display in String format
     * @param idGrid grid id
     * @param idSquare square id on grid (grid id)
     */
    fun insertValueByUser(sValue: String, idGrid: Int, idSquare: Int) {
        if (sValue != "0") {

            val newValue = sValue.toInt()
            val index = getIndex(idGrid, idSquare)

            Log.d("tom971", "insertValueByUser $mIdMode")

            if (mIdMode == MODE_INSERT) {
                val oldValue = sudokuData[index].value

                if (oldValue != 0) {
                    removeValue(oldValue, index)
                }

                if (newValue != oldValue) {
                    addValue(newValue, mIdColorRes, index)
                }

                saveState(newValue, idGrid, idSquare, mIdColorRes)

                state.postValue(
                    SudokuState.FillSquare(
                        sudokuData.sudoku,
                        R.string.insert_value,
                        newValue,
                        caretaker.isFirstItem(),
                        caretaker.isLastItem(),
                        getListSquareByIdGrid(idGrid)
                    )
                )
            } else if (mIdMode == MODE_UPDATE) {
                sudokuData.updatePossibilty(index, newValue)
                state.postValue(SudokuState.ModifyPossibility(sudokuData.sudoku))
            }
        }

    }

    /**
     * if digits to find is 0, sudoku is solved
     * else we search value
     */
    fun startAlgo() {
        if (sudokuData.digitsToFind == 0) {
            state.postValue(SudokuState.DisplayMessage(R.string.success_sudoku))
        } else if (!searchValue()) {
            state.postValue(SudokuState.DisplayMessage(R.string.error_sudoku))
        }
    }

    /**
     * save state with memento pattern
     *
     * @param value
     * @param idGrid
     * @param idSquare
     */
    private fun saveState(value: Int, idGrid: Int, idSquare: Int, idResColor: Int) {
        originator.setState(idGrid, idSquare, value, idResColor)
        caretaker.addMemento(originator.save())
    }

    /**
     * get the previous memento and refresh sudoku
     */
    fun setPreviousState() {
        val memento = caretaker.getMemento(-1)
        removeValue(memento.value, getIndex(memento.idGrid, memento.idSquare))
        state.postValue(
            SudokuState.RestoreState(
                sudokuData.sudoku, caretaker.isFirstItem(),
                caretaker.isLastItem()
            )
        )
    }

    /**
     * get the next memento and refresh sudoku
     */
    fun setNextState() {
        val memento = caretaker.getMemento(+1)
        addValue(memento.value, memento.idResColor, getIndex(memento.idGrid, memento.idSquare))
        state.postValue(
            SudokuState.RestoreState(
                sudokuData.sudoku, caretaker.isFirstItem(),
                caretaker.isLastItem()
            )
        )
    }

    /**
     * search first value possible to insert in sudoku
     *
     * @return true if a value is found
     */
    private fun searchValue(): Boolean {
        if (checkOneValueBySquare()) {
            return true
        }
        if (checkOneValue9Time(
                ::getStartIndexGridByPosition,
                ::getIndexInGrid,
                R.string.one_value_by_grid
            )
        ) {
            return true
        }
        if (checkOneValue9Time(
                ::getStartIndexRowByPosition,
                ::getIndexInRow,
                R.string.one_value_by_row
            )
        ) {
            return true
        }
        if (checkOneValue9Time(
                ::getStartIndexColumnByPosition,
                ::getIndexInColumn,
                R.string.one_value_by_column
            )
        ) {
            return true
        }
        if (checkPair()) {
            return true
        }
        if (checkIntersection()) {
            return true
        }
        return false
    }

    /**
     * for each index in grid, we keep every index by value
     * value in [0,8], to get real value, add 1
     * value -> {index...}
     *
     * index is square index who can have value
     *
     * and search an intersection
     *
     * @return true if there is an intersection
     */
    private fun checkIntersection(): Boolean {
        for (idGrid in 0 until 9) {
            val tabCompteur = Array<MutableSet<Int>>(9) { mutableSetOf() }
            val startIndex = getStartIndexGridByPosition(idGrid)
            for (indexSquare in 0 until 9) {
                val index = getIndexInGrid(startIndex, indexSquare)
                if (sudokuData[index].value == 0) {
                    sudokuData[index].possibility.forEach {
                        tabCompteur[it - 1].add(index)
                    }
                }
            }

            if (checkIntersectionBy(
                    tabCompteur,
                    ::getStartIndexRow,
                    ::getIndexInRow,
                    R.string.intersection_row,
                    isRow = true, isColunm = false
                )
            ) {
                return true
            }

            if (checkIntersectionBy(
                    tabCompteur,
                    ::getStartIndexColumn,
                    ::getIndexInColumn,
                    R.string.intersection_column,
                    isRow = false, isColunm = true
                )
            ) {
                return true
            }

        }

        return false
    }

    /**
     *  search an intersection by grid
     *  if a value is on 2 or 3 squares, who can contains the value
     *  we check if thoses square are on the same row or colunms
     *  if true, we remove this value from others squares from others grids on the same row or colunm
     *
     * @param tabCompteur
     * @param getStartIndex function getStartIndexRow | getStartIndexColumn
     * @param getIndexFor function getIndexInRow | getIndexInColumn
     * @param idRes id ressource string
     * @return true if there a modification on the sudoku
     */
    private fun checkIntersectionBy(
        tabCompteur: Array<MutableSet<Int>>,
        getStartIndex: (index: Int) -> Int,
        getIndexFor: (startIndex: Int, position: Int) -> Int,
        idRes: Int,
        isRow: Boolean,
        isColunm: Boolean
    ): Boolean {
        for (i in 0 until 9) {
            val setIndex = mutableSetOf<Int>()
            if (tabCompteur[i].size in 2..3) {
                tabCompteur[i].forEach {
                    setIndex.add(getStartIndex(it))
                }

                if (setIndex.size == 1) {

                    val index = setIndex.toList()[0]
                    val result = updateDigitsAvailableInterserction(
                        i + 1,
                        tabCompteur[i],
                        index,
                        getIndexFor
                    )

                    if (result) {
                        val setIndexSelectedToRemove = getImpactedIndex(
                            tabCompteur[i].toList()[0], checkGrid = true,
                            checkColumn = isColunm, checkRow = isRow
                        )

                        val setIndexSelectedToKeep = mutableSetOf<Int>()
                        tabCompteur[i].forEach {
                            setIndexSelectedToKeep.add(it)
                            setIndexSelectedToRemove.remove(it)
                        }

                        state.postValue(
                            SudokuState.SuccessAlgo(
                                sudokuData.sudoku,
                                idRes,
                                mutableSetOf(i + 1),
                                convertSetIndexToLIstPair(setIndexSelectedToKeep),
                                convertSetIndexToLIstPair(setIndexSelectedToRemove)
                            )
                        )

                        return true
                    }
                }
            }
        }

        return false
    }

    /**
     * update possibility on sudoku after an intersection is found
     *
     * @param value
     * @param setIndex
     * @param startIndex of row | column
     * @param getIndexFor function getIndexInRow | getIndexInColumn
     * @return
     */
    private fun updateDigitsAvailableInterserction(
        value: Int, setIndex: MutableSet<Int>, startIndex: Int,
        getIndexFor: (startIndex: Int, position: Int) -> Int
    ): Boolean {
        var result = false
        for (position in 0 until 9) {
            val index = getIndexFor(startIndex, position)
            if (sudokuData[index].value == 0 && !setIndex.contains(index)) {
                val resultRemove = remove(sudokuData[index], value)
                if (resultRemove) {
                    result = true
                }
            }
        }
        return result
    }

    /**
     * add value in sudoku
     * and update other squares in the same row, column and grid
     *
     * @param value value to put in the square
     * @param idTextColor id ressource for textColor
     * @param index square index where we add the value
     */
    private fun addValue(value: Int, idTextColor: Int, index: Int) {
        sudokuData.addValue(index, value, idTextColor)
        updateDigitsAvailable(value, getStartIndexGrid(index), ::getIndexInGrid, ::remove)
        updateDigitsAvailable(value, getStartIndexRow(index), ::getIndexInRow, ::remove)
        updateDigitsAvailable(value, getStartIndexColumn(index), ::getIndexInColumn, ::remove)
    }

    /**
     * remove value in sudoku
     * and update other squares in the same row, column and grid
     *
     * @param value value to remove in the square
     * @param index square index where we remove the value
     */
    private fun removeValue(value: Int, index: Int) {
        sudokuData.removeValue(index, value)
        updateDigitsAvailable(value, getStartIndexColumn(index), ::getIndexInColumn, ::add)
        updateDigitsAvailable(value, getStartIndexRow(index), ::getIndexInRow, ::add)
        updateDigitsAvailable(value, getStartIndexGrid(index), ::getIndexInGrid, ::add)

        for (i in sudokuData.sudoku.indices) {
            val v = sudokuData[i].value
            if (v != 0) {
                updateDigitsAvailable(v, getStartIndexGrid(i), ::getIndexInGrid, ::remove)
                updateDigitsAvailable(v, getStartIndexRow(i), ::getIndexInRow, ::remove)
                updateDigitsAvailable(v, getStartIndexColumn(i), ::getIndexInColumn, ::remove)
            }
        }
    }

    /**
     * update digits available in square by grid || row || column
     *
     * @param value in [1,9]
     * @param startIndex getStartIndexGrid || getStartIndexRow || getStartIndexColumn
     * @param getIndex getIndexForGrid || getIndexForRow || getIndexForColumn
     * @param action add || remove
     */
    private fun updateDigitsAvailable(
        value: Int,
        startIndex: Int,
        getIndex: (start: Int, index: Int) -> Int,
        action: (squareData: SquareData, value: Int) -> Boolean
    ) {
        for (i in 0 until 9) {
            val index = getIndex(startIndex, i)
            if (sudokuData[index].value != value) {
                action(sudokuData[index], value)
            }
        }
    }

    /**
     * remove the digit from the list of posible digits in the selected square
     *
     * @param squareData selected square
     * @param value to remove
     */
    private fun remove(squareData: SquareData, value: Int): Boolean {
        return squareData.possibility.remove(value)
    }

    /**
     * add the digit in the list of posible digits in the selected square
     *
     * @param squareData selected square
     * @param value to add
     */
    private fun add(squareData: SquareData, value: Int): Boolean {
        return squareData.possibility.add(value)
    }

    /**
     * find fist square with a value only available in the set
     * if square is found, update view
     *
     * @return true if square is found else false
     */
    private fun checkOneValueBySquare(): Boolean {
        var result = false
        for (index in sudokuData.sudoku.indices) {
            if (sudokuData[index].value == 0 && sudokuData[index].possibility.size == 1) {
                val value = sudokuData[index].possibility.toList()[0]
                addValue(value, R.color.colorValueFound, index)
                saveState(
                    value,
                    getIndexGrid(index),
                    getIndexSquareInGrid(index),
                    R.color.colorValueFound
                )

                val setIndexSelectedToRemove = getImpactedIndex(
                    index, checkGrid = true,
                    checkColumn = true, checkRow = true
                )

                setIndexSelectedToRemove.remove(index)

                state.postValue(
                    SudokuState.SuccessAlgo(
                        sudokuData.sudoku,
                        R.string.one_value_by_square,
                        mutableSetOf(value),
                        convertSetIndexToLIstPair(mutableSetOf(index)),
                        convertSetIndexToLIstPair(setIndexSelectedToRemove)
                    )
                )

                return true
            }
        }

        return result
    }

    private fun getImpactedIndex(
        index: Int,
        checkGrid: Boolean,
        checkColumn: Boolean,
        checkRow: Boolean
    ): MutableSet<Int> {
        val setImpactedIndex = mutableSetOf<Int>()

        if (checkGrid) {
            val indexGrid = getStartIndexGrid(index)
            for (position in 0 until 9) {
                setImpactedIndex.add(getIndexInGrid(indexGrid, position))
            }
        }

        if (checkColumn) {
            val indexColumn = getStartIndexColumn(index)
            for (position in 0 until 9) {
                setImpactedIndex.add(getIndexInColumn(indexColumn, position))
            }
        }

        if (checkRow) {
            val indexRow = getStartIndexRow(index)
            for (position in 0 until 9) {
                setImpactedIndex.add(getIndexInRow(indexRow, position))
            }
        }


        return setImpactedIndex
    }

    /**
     * find fist (row || column || grid) with a digit only available in one square from (row || column || grid)
     * if square is found, update view
     *
     * @param idRes id String to explain result
     * @param getIndexIn getIndexInGrid | getIndexInRow | getIndexInColunm
     * @return true if square is found else false
     */
    private fun checkOneValue9Time(
        getIndexByPosition: (position: Int) -> Int,
        getIndexIn: (start: Int, index: Int) -> Int,
        idRes: Int
    ): Boolean {
        for (i in 0 until 9) {
            if (checkOneValue(getIndexByPosition(i), getIndexIn, idRes)) {
                return true
            }
        }
        return false
    }

    /**
     * find fist square with a value only available in the row || column || grid
     * if square is found, update view
     *
     * @param idRes id String to explain result
     *
     * @return true if square is found else false
     */
    private fun checkOneValue(
        startIndex: Int,
        getIndex: (start: Int, index: Int) -> Int,
        idRes: Int
    ): Boolean {
        val tabCompteur = Array<MutableSet<Int>>(9) { mutableSetOf() }
        for (position in 0 until 9) {
            val index = getIndex(startIndex, position)

            if (sudokuData[index].value == 0) {
                sudokuData[index].possibility.forEach {
                    tabCompteur[it - 1].add(index)
                }
            }
        }

        for (i in tabCompteur.indices) {
            if (tabCompteur[i].size == 1) {
                val value = i + 1
                val index = tabCompteur[i].toList()[0]
                addValue(value, R.color.colorValueFound, index)
                saveState(
                    value,
                    getIndexGrid(index),
                    getIndexSquareInGrid(index),
                    R.color.colorValueFound
                )
                val setIndexSelectedToRemove = getImpactedIndex(
                    index, checkGrid = true,
                    checkColumn = true, checkRow = true
                )

                setIndexSelectedToRemove.remove(index)

                state.postValue(
                    SudokuState.SuccessAlgo(
                        sudokuData.sudoku, idRes, mutableSetOf(value),
                        convertSetIndexToLIstPair(mutableSetOf(index)),
                        convertSetIndexToLIstPair(setIndexSelectedToRemove)
                    )
                )

                return true
            }
        }

        return false
    }

    /**
     * convert set of Index to list of Pair (IdGrid, IdSquare)
     *
     * @param setIndex
     * @return list of pair
     */
    private fun convertSetIndexToLIstPair(setIndex: MutableSet<Int>): MutableList<Pair<Int, Int>> {
        val listPair = mutableListOf<Pair<Int, Int>>()
        setIndex.forEach {
            listPair.add(Pair(getIndexGrid(it), getIndexSquareInGrid(it)))
        }
        return listPair
    }

    /**
     * return square index in row if the square contains this value on his possibility
     *
     * @param startIndex will be this number : 0, 8, 18, 27, ..., 72
     * @param value to insert in square
     */
    private fun findIndexByRow(startIndex: Int, value: Int): Int {
        for (i in 0 until 9) {
            var index = startIndex + i
            val squareData = sudokuData[index]
            if (squareData.value == 0 && squareData.possibility.contains(value)) {
                return index
            }
        }
        return -1
    }

    /**
     * return square index in column if the square contains this value on his possibility
     *
     * @param startIndex will be this number : 0, 1, 2, 3, ..., 8
     * @param value to insert in square
     */
    private fun findIndexByColumn(startIndex: Int, value: Int): Int {
        for (i in 0 until 9) {
            var index = startIndex + i * 9
            val squareData = sudokuData[index]
            if (squareData.value == 0 && squareData.possibility.contains(value)) {
                return index
            }
        }
        return -1
    }

    /**
     * return square index in grid if the square contains this value on his possibility
     *
     * @param startIndex will be this number : 0, 3, 6, 27, ..., 60
     * @param value to insert in square
     */
    private fun findIndexByGrid(startIndex: Int, value: Int): Int {
        for (i in 0 until 9) {
            var index = startIndex + (i % 3) + ((i / 3) * 9)
            val squareData = sudokuData[index]
            if (squareData.value == 0 && squareData.possibility.contains(value)) {
                return index
            }
        }
        return -1
    }

    /**
     * search a square who contains only 2 possibilities
     * when it's found, search if there is an other square
     * on the grid | row | colunm who contains thoses 2 values
     * with this function checkPairByGrid
     *
     * @return true if pair is found
     */
    private fun checkPair(): Boolean {
        for (index in sudokuData.sudoku.indices) {
            val square = sudokuData[index]
            if (square.value == 0 && square.possibility.size == 2) {
                if (checkPairByGrid(index)) {
                    return true
                }
                if (checkPairBy(
                        index, getStartIndexRow(index), ::getIndexInRow,
                        isColunm = false,
                        isRow = true,
                        idResString = R.string.pair_found_row
                    )
                ) {
                    return true
                }
                if (checkPairBy(
                        index, getStartIndexColumn(index), ::getIndexInColumn,
                        isColunm = true,
                        isRow = false,
                        idResString = R.string.pair_found_coloumn
                    )
                ) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * search if there is an other square
     * on the grid  who contains thoses 2 values
     * @param indexPair
     * @return true if pair is found
     */
    private fun checkPairByGrid(indexPair: Int): Boolean {
        val startIndexGrid = getStartIndexGrid(indexPair)
        var setIndex = mutableSetOf<Int>()

        setIndex.addAll(
            getSquaresContainsPair(
                startIndexGrid,
                indexPair,
                ::getIndexInGrid,
                ::containsOnlyPair
            )
        )

        if (setIndex.size == 1) {
            var idResString: Int = R.string.pair_found_grid
            val indexOtherPair = setIndex.toList()[0]

            val setIndexSelected = mutableSetOf<Int>()

            for (position in 0 until 9) {
                setIndexSelected.add(getIndexInGrid(startIndexGrid, position))
            }

            var isModificationGrid = updateDigitsAvailableForPair(
                startIndexGrid,
                indexPair,
                indexOtherPair,
                ::getIndexInGrid
            )

            val startIndexColumn = getStartIndexColumn(indexPair)
            var updateColumn = false
            if (startIndexColumn == getStartIndexColumn(indexOtherPair)) {
                updateColumn = updateDigitsAvailableForPair(
                    startIndexColumn,
                    indexPair,
                    indexOtherPair,
                    ::getIndexInColumn
                )
                idResString = R.string.pair_found_grid_column
            }

            val startIndexRow = getStartIndexRow(indexPair)

            var updateRow = false
            if (startIndexRow == getStartIndexRow(indexOtherPair)) {
                updateRow = updateDigitsAvailableForPair(
                    startIndexRow,
                    indexPair,
                    indexOtherPair,
                    ::getIndexInRow
                )
                idResString = R.string.pair_found_grid_row
            }

            var setIndexSelectedToRemove = getImpactedIndex(
                indexPair, checkGrid = true,
                checkColumn = updateColumn, checkRow = updateRow
            )

            setIndexSelectedToRemove.remove(indexPair)
            setIndexSelectedToRemove.remove(indexOtherPair)

            val setIndexSelectedTKeep = mutableSetOf(indexPair, indexOtherPair)

            return if (isModificationGrid) {
                postValuePair(
                    indexPair,
                    convertSetIndexToLIstPair(setIndexSelectedTKeep),
                    convertSetIndexToLIstPair(setIndexSelectedToRemove),
                    idResString
                )
                true
            } else {
                false
            }
        }

        return false
    }

    /**
     * search if there is an other square
     * on the row | colunm  who contains thoses 2 values
     *
     * @param indexPair
     * @param startIndex row | column
     * @param getIndexFor function getIndexInRow | getIndexInColumn
     * @return true if pair is found
     */
    private fun checkPairBy(
        indexPair: Int,
        startIndex: Int,
        getIndexFor: (start: Int, position: Int) -> Int,
        isColunm: Boolean,
        isRow: Boolean,
        idResString: Int
    ): Boolean {
        var setIndex = mutableSetOf<Int>()

        setIndex.addAll(
            getSquaresContainsPair(
                startIndex,
                indexPair,
                getIndexFor,
                ::containsOnlyPair
            )
        )

        if (setIndex.size == 1) {
            val indexOtherPair = setIndex.toList()[0]

            var isModification = updateDigitsAvailableForPair(
                startIndex,
                indexPair,
                indexOtherPair,
                getIndexFor
            )
            return if (isModification) {

                var setIndexSelectedToRemove = getImpactedIndex(
                    startIndex, checkGrid = false,
                    checkColumn = isColunm, checkRow = isRow
                )

                setIndexSelectedToRemove.remove(indexPair)
                setIndexSelectedToRemove.remove(indexOtherPair)

                val setIndexSelectedTKeep = mutableSetOf(indexPair, indexOtherPair)

                postValuePair(
                    indexPair,
                    convertSetIndexToLIstPair(setIndexSelectedTKeep),
                    convertSetIndexToLIstPair(setIndexSelectedToRemove),
                    idResString
                )
                true
            } else {
                false
            }
        }

        return false
    }

    /**
     * we update the state to refresh the view
     *
     * @param indexPair
     * @param listIndexToKeep
     * @param listIndexToRemove
     */
    private fun postValuePair(
        indexPair: Int,
        listIndexToKeep: MutableList<Pair<Int, Int>>,
        listIndexToRemove: MutableList<Pair<Int, Int>>,
        idRes: Int
    ) {
        state.postValue(
            SudokuState.SuccessAlgo(
                sudokuData.sudoku,
                idRes,
                sudokuData[indexPair].possibility,
                listIndexToKeep,
                listIndexToRemove
            )
        )
    }

    /**
     * update digits available in square by grid | row | column
     *
     * @param startIndex
     * @param indexPair
     * @param indexOtherPair
     * @param getIndexFor
     * @return
     */
    private fun updateDigitsAvailableForPair(
        startIndex: Int, indexPair: Int, indexOtherPair: Int,
        getIndexFor: (start: Int, position: Int) -> Int
    ): Boolean {
        var isModification = false
        for (i in 0 until 9) {
            val index = getIndexFor(startIndex, i)

            var result = false
            if (sudokuData[index].value == 0 && index != indexPair && index != indexOtherPair) {
                result = removeValuePair(index, indexPair)
            }
            if (result) {
                isModification = true
            }
        }
        return isModification
    }

    /**
     * search a square who contains the pair
     *
     * @param startIndex
     * @param indexPair
     * @param getIndexFor
     * @param contains
     * @return
     */
    private fun getSquaresContainsPair(
        startIndex: Int,
        indexPair: Int,
        getIndexFor: (start: Int, position: Int) -> Int,
        contains: (Set<Int>, Set<Int>) -> Boolean
    ):
            MutableSet<Int> {
        var setIndex = mutableSetOf<Int>()
        for (i in 0 until 9) {
            val index = getIndexFor(startIndex, i)
            if (indexPair != index) {
                if (contains(sudokuData[index].possibility, sudokuData[indexPair].possibility)) {
                    setIndex.add(index)
                }
            }
        }
        return setIndex
    }

    /**
     * check if setPossibility contains 2 values and is identical to setPossibilityPair
     *
     * @param setPossibility
     * @param setPossibilityPair
     */
    @VisibleForTesting
    fun containsOnlyPair(setPossibility: Set<Int>, setPossibilityPair: Set<Int>) =
        setPossibility.size == 2 && setPossibility.containsAll(setPossibilityPair)

    /**
     * remove values from square indexPair in square index
     * true if there are values to remove
     *
     * @param index
     * @param indexPair
     * @return true if there are some values to remove
     */
    @VisibleForTesting
    fun removeValuePair(index: Int, indexPair: Int): Boolean {
        return sudokuData[index].possibility.removeAll(sudokuData[indexPair].possibility)
    }

    /**
     * compute the first index of column with position
     *
     * @param position in [0,8]
     * @return value in [0,8]
     */
    @VisibleForTesting
    fun getStartIndexColumnByPosition(position: Int): Int = position

    /**
     * compute the first index of grid with position
     *
     * @param position in [0,8]
     * @return one value in this set {0, 3, 6, 27, 30, 33, 54, 57, 60}
     */
    @VisibleForTesting
    fun getStartIndexGridByPosition(position: Int): Int =
        (3 * position) + (9 * 2 * (position / 3))

    /**
     * compute the first square index of row with position
     *
     * @param position in [0,8]
     * @return one value in this set {0, 9, 18, 27, 36, 45, 54, 63, 72}
     */
    @VisibleForTesting
    fun getStartIndexRowByPosition(position: Int): Int = position * 9

    /**
     * compute the square index of row with startIndex & position
     *
     * @param startIndex in {0, 9, 18, 36, 45, 54, 63, 72}
     * @param position in [0,8]
     */
    @VisibleForTesting
    fun getIndexInRow(startIndex: Int, position: Int): Int = startIndex + position

    /**
     * compute index of column with startIndex & position
     *
     * @param startIndex in [0,8]
     * @param position in [0,8]
     */
    @VisibleForTesting
    fun getIndexInColumn(startIndex: Int, position: Int): Int = startIndex + position * 9

    /**
     * compute index in grid with startIndex & position
     *
     * @param startIndex in {0, 3, 6, 27, 30, 33, 54, 57, 60}
     * @param position in [0,8]
     */
    @VisibleForTesting
    fun getIndexInGrid(startIndex: Int, position: Int): Int =
        startIndex + (position % 3) + ((position / 3) * 9)

    /**
     * compute first square index in row who contains index
     * * if index is 70, return 63
     * @param index in  sudoku
     */
    @VisibleForTesting
    fun getStartIndexRow(index: Int) = (index / 9) * 9

    /**
     * compute first square index in column who contains index
     * * if index is 70, return 7
     * @param index in  sudoku
     */
    @VisibleForTesting
    fun getStartIndexColumn(index: Int) = index % 9

    /**
     * compute first square index in grid who contains index
     * * if index is 70, return 60
     * @param index in  sudoku
     */
    @VisibleForTesting
    fun getStartIndexGrid(index: Int): Int {
        val column = index % 9
        val row = index / 9
        return (column / 3) * 3 + ((row / 3) * 3) * 9
    }

    /**
     * convert grid index and square index to get the index in Sudoku
     * @param idGrid id of selected grid
     * @param idSquare id of selected square in the grid
     */
    @VisibleForTesting
    fun getIndex(idGrid: Int, idSquare: Int) =
        (3 * (idGrid % 3) + (idSquare % 3)) + (((idSquare / 3) + (idGrid / 3) * 3) * 9)

    /**
     * compute grid index with index
     * * if index is 70, return 8
     * @param index in  sudoku
     */
    @VisibleForTesting
    fun getIndexGrid(index: Int): Int {
        val indexGrid = getStartIndexGrid(index)

        return ((indexGrid % 9) / 3) + (indexGrid / 9)
    }

    /**
     * compute square index in grid with index
     * if index is 70, return 4
     * @param index in  sudoku
     */
    @VisibleForTesting
    fun getIndexSquareInGrid(index: Int): Int {
        val indexRow = getStartIndexRow(index)
        val indexColumn = getStartIndexColumn(index)

        return (((indexRow / 9) % 3) * 3) + (indexColumn % 3)
    }

    /**
     * return squares who compose the grid at position idGrid
     *
     * @param idGrid in sudoku
     * @return list of square data
     */
    @VisibleForTesting
    fun getListSquareByIdGrid(idGrid: Int): MutableList<SquareData> {
        val listSquareData = mutableListOf<SquareData>()
        val startIndexGrid = getStartIndexGridByPosition(idGrid)
        for (position in 0 until 9) {
            val idSquare = getIndexInGrid(startIndexGrid, position)
            listSquareData.add(sudokuData[idSquare])
        }
        return listSquareData
    }

    fun computeListSquareByIdGrid(idGrid: Int) {
        state.postValue(SudokuState.RefreshTouchPad(getListSquareByIdGrid(idGrid)))
    }

    fun updateColor(idRes: Int) {
        mIdColorRes = idRes
    }

    fun updateMode(@StateMode idMode: Int) {
        mIdMode = idMode
        Log.d("tom971", "updateMode $mIdMode")
    }

}
