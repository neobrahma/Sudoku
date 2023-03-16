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
    private val listGridViewSelected = mutableListOf<GridView>()
    private val listGridView: MutableList<GridView> = mutableListOf()

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
            val grid = it as GridView
            listGridView.add(grid)
            grid.setOnGridListener(object : GridView.OnGridListener {
                override fun onClickSquare(position: Int) {
                    if (!listGridViewSelected.contains(grid)) {
                        listGridViewSelected.forEach { gridViewSelected ->
                            gridViewSelected.unSelectedSquare()
                        }
                        listGridViewSelected.clear()
                    }
                    listGridViewSelected.add(grid)
                    onSudokuListener?.onClickSquare(grid.tag.toString().toInt(), position)
                }
            })
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

        unSelectSquare()
    }

    fun selectSquare(listSquareSelected: List<Pair<Int, Int>>, listValueSelected: List<Int>, idResColor : Int) {
        listSquareSelected.forEach {
            val grid = listGridView[it.first]
            listGridViewSelected.add(grid)
            grid.selectSquare(it.second, listValueSelected, idResColor)
        }
    }

    fun unSelectSquare(){
        listGridViewSelected.forEach {
            it.unSelectedSquare()
        }

        listGridViewSelected.clear()
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

    interface OnSudokuListener {
        fun onClickSquare(idGrid: Int, idSquare: Int)
    }

}