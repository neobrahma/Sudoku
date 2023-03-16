package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.GridLayout
import androidx.core.view.forEach
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData

class GridView : GridLayout {

    private var onGridListener: OnGridListener? = null
    private val listSquareView: MutableList<SqareView> = mutableListOf()
    private val listSquareViewSelected: MutableList<SqareView> = mutableListOf()

    private val onClickListener = OnClickListener { v ->
        when (v) {
            is SqareView -> {
                unSelectedSquare()
                listSquareViewSelected.add(v)
                v.selectSquare()
                onGridListener?.onClickSquare((v.tag as String).toInt())
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
        LayoutInflater.from(context).inflate(R.layout.view_grid, this, true)
        columnCount = 3

        forEach {
            listSquareView.add(it as SqareView)
            it.setOnClickListener(onClickListener)
        }
    }

    fun setOnGridListener(listener: OnGridListener) {
        onGridListener = listener
    }

    fun updateGrid(square: Int, solution: SquareData) {
        listSquareView[square].updateSquare(solution)
        unSelectedSquare()
    }

    fun unSelectedSquare() {
        listSquareViewSelected.forEach {
            it.unSelectSquare()
        }
        listSquareViewSelected.clear()
    }

    fun selectSquare(idSquare: Int, listValueSelected: List<Int>, idResColor : Int) {
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

    interface OnGridListener {
        fun onClickSquare(position: Int)
    }

}