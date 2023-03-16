package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import androidx.core.view.forEach
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData

class SudokuView : GridLayout {

    private var onSudokuListener: OnSudokuListener? = null
    private val listGridViewSelected = mutableListOf<GridViewBis>()
    private val listGridView: MutableList<GridViewBis> = mutableListOf()

    private val onGridListener = object : GridViewBis.OnGridListener {
        override fun onClickSquare(view: GridViewBis, position: Int) {
            onSudokuListener?.onClickSquare(view.tag.toString().toInt(), position)
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.view_sudoku, this, true)
        columnCount = 3

        forEach {
            val grid = it as GridViewBis
            listGridView.add(grid)
            grid.setOnGridListener(onGridListener)
        }
    }

    fun setOnSudokuListener(listener: OnSudokuListener) {
        onSudokuListener = listener
    }

    fun updateSudoku(solution: Array<SquareData>) {
        for (i in solution.indices) {
            val posLine = i / 9
            val posColumn = i % 9
            val square = (posColumn % 3) + (posLine % 3) * 3
            val grid = (posColumn / 3) + (posLine / 3) * 3

            listGridView[grid].updateGrid(square, solution[i])
        }
    }

    fun selectSquare(
        listSquareSelected: List<Pair<Int, Int>>,
        listValueSelected: List<Int>,
        idResColor: Int
    ) {
        listSquareSelected.forEach {
            val grid = listGridView[it.first]
            listGridViewSelected.add(grid)
            grid.selectSquare(it.second, listValueSelected, idResColor)
        }
    }


    fun enlightenedValue(value: Int) {
        listGridView.forEach {
            it.enlightenedValue(value)
        }
    }

    fun unEnlightenedValue() {
        listGridView.forEach {
            it.unEnlightenedValue()
        }
    }

    fun selectGrid(idGrid: Int) {
        listGridViewSelected.add(listGridView[idGrid])
        listGridView[idGrid].selectGrid()
    }

    fun unSelectGrid(idGrid: Int) {
        listGridView[idGrid].unSelectGrid()
        listGridViewSelected.clear()
    }

    fun selectSquare(idSquare: Int) {
        listGridViewSelected.forEach {
            it.selectSquare(idSquare)
        }
    }

    fun unSelectSquare(idSquare: Int) {
        listGridViewSelected.forEach {
            it.unSelectSquare(idSquare)
        }
    }

    fun unSelectSquare() {
        listGridViewSelected.forEach {
            it.unSelectedSquare()
        }
    }

    interface OnSudokuListener {
        fun onClickSquare(idGrid: Int, idSquare: Int)
    }

}