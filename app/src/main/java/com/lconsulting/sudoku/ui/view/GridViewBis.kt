package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.GridLayout
import androidx.core.view.forEach
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData

class GridViewBis : GridLayout {

    private var onGridListener: OnGridListener? = null
    private val listSquareView: MutableList<ISquareView> = mutableListOf()
    private val listSquareViewSelected: MutableList<ISquareView> = mutableListOf()

    private val onClickListener = OnClickListener { v ->
        when (v) {
            is ISquareView -> {
                onGridListener?.onClickSquare(this@GridViewBis, (v.tag as String).toInt())
            }
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.view_grid_bis, this, true)
        columnCount = 3

        forEach {
            listSquareView.add(it as ISquareView)
            it.setOnClickListener(onClickListener)
        }
    }

    fun setOnGridListener(listener: OnGridListener) {
        onGridListener = listener
    }

    fun updateGrid(square: Int, solution: SquareData) {
        listSquareView[square].updateSquare(solution)
    }

    fun unSelectedSquare() {
        listSquareViewSelected.forEach {
            it.unSelectSquare()
        }
        listSquareViewSelected.clear()
    }

    fun selectSquare(idSquare: Int, listValueSelected: List<Int>, idResColor: Int) {
        val square = listSquareView[idSquare]
        listSquareViewSelected.add(square)
        square.selectSquare(listValueSelected, idResColor)

    }

    fun enlightenedValue(value: Int) {
        listSquareView.forEach {
            it.enlightenedValue(value)
        }
    }

    fun unEnlightenedValue() {
        listSquareView.forEach {
            it.unEnlightenedValue()
        }
    }

    /** NEW FUNCTION **/

    fun selectGrid() {
        background = resources.getDrawable(R.drawable.background_grid_selected)
    }

    fun unSelectGrid() {
        background = resources.getDrawable(R.drawable.background_square)
    }

    fun selectSquare(idSquare: Int) {
        listSquareViewSelected.add(listSquareView[idSquare])
        listSquareView[idSquare].selectSquare()
    }

    fun unSelectSquare(idSquare: Int) {
        listSquareViewSelected.forEach {
            it.unSelectSquare()
        }
        listSquareViewSelected.clear()
    }

    interface OnGridListener {
        fun onClickSquare(view: GridViewBis, position: Int)
    }

}