package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import androidx.core.view.forEach
import com.lconsulting.sudoku.data.SquareData

class TouchPadSubLevelView : TouchPadView {

    private val onGridViewListener = object : GridView.GridViewListener {
        override fun onClickValue(value: Int) {
            mTouchPadViewListener?.onSelectValue(value)
        }
    }

    private var mListSquareData: MutableList<SquareData>? = null

    private var mIdSquare: Int = -1

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    override fun initValues() {
        forEach {
            when (it) {
                is GridView -> {
                    subLevelView = it
                    it.setGridViewListener(onGridViewListener)
                }
                else -> initValue(it)
            }
        }
    }

    override fun createOnClickListener() {
        mOnClickListener = OnClickListener {
            when (it) {
                is SquareView -> {
                    mIdSquare = it.tag.toString().toInt() - 1
                    (subLevelView as GridView).refreshValues(mListSquareData!![mIdSquare].possibility)
                    mTouchPadViewListener?.onSelectIdSquare(mIdSquare)
                    openTouchPad(listConstraint[mIdSquare])
                }
            }
        }
    }

    override fun close() {
        mTouchPadViewListener?.onUnSelectIdSquare(mIdSquare)
        mIdSquare = -1
        closeTouchPad()
    }

    override fun refreshValues(listSquareData: MutableList<SquareData>) {
        mListSquareData = listSquareData
        listSquareView.forEachIndexed { index, squareView ->
            squareView.displayValue(listSquareData[index])
        }
    }


}