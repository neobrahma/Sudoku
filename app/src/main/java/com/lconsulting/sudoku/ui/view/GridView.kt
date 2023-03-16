package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import com.lconsulting.sudoku.R

class GridView : ConstraintLayout {

    private val mListSquareView: MutableList<SquareView> = mutableListOf()

    private var mListener: GridViewListener? = null

    private var mPossibility: MutableSet<Int>? = null

    private val onClickListener = OnClickListener {
        val valueSelected = it.tag.toString().toInt()
        mListener?.onClickValue(valueSelected)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.view_grid, this, true)
        forEach {
            when (it) {
                is SquareView -> {
                    mListSquareView.add(it)
                    it.setOnClickListener(onClickListener)
                }
            }
        }
    }

    fun refreshValues(possibility: MutableSet<Int>) {
        mPossibility = possibility
        mListSquareView.forEach { squareView ->
            val valueString = squareView.tag.toString()
            val valueInt = valueString.toInt()
            squareView.displayValue(
                valueString,
                possibility.contains(valueInt)
            )
        }
    }

    fun setGridViewListener(listener: GridViewListener) {
        mListener = listener
    }

    interface GridViewListener {
        fun onClickValue(value: Int)
    }
}