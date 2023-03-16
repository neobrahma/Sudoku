package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import androidx.core.view.forEach
import com.lconsulting.sudoku.data.SquareData

class TouchPadMasterLevelView : TouchPadView {

    private var idGridSelected = 0

    private val onTouchPadViewListener = object : TouchPadViewListener {
        override fun onSelectIdGrid(idGrid: Int) {
        }

        override fun onSelectIdSquare(idSquare: Int) {
            mTouchPadViewListener?.onSelectIdSquare(idSquare)
        }

        override fun onSelectValue(value: Int) {
            mTouchPadViewListener?.onSelectValue(value)
        }

        override fun onUnSelectIdGrid(idGrid: Int) {

        }

        override fun onUnSelectIdSquare(idSquare: Int) {
            mTouchPadViewListener?.onUnSelectIdSquare(idSquare)
        }
    }

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    override fun initValues() {
        forEach {
            when (it) {
                is TouchPadSubLevelView -> {
                    subLevelView = it
                    it.setTouchPadViewListener(onTouchPadViewListener)
                }
                else -> initValue(it)
            }
        }
    }

    override fun createOnClickListener() {
        mOnClickListener = OnClickListener {
            when (it) {
                is SquareView -> {
                    idGridSelected = it.tag.toString().toInt() - 1
                    mTouchPadViewListener?.onSelectIdGrid(idGridSelected)
                }
            }
        }
    }

    override fun close() {
        if (isOpen()) {
            if ((subLevelView as TouchPadSubLevelView).isOpen()) {
                (subLevelView as TouchPadSubLevelView).close()
            } else {
                mTouchPadViewListener?.onUnSelectIdGrid(idGridSelected)
                closeTouchPad()
            }
        }
    }

    override fun refreshValues(listSquareData: MutableList<SquareData>) {
        (subLevelView as TouchPadSubLevelView).refreshValues(listSquareData)
    }

    fun open(listSquareData: MutableList<SquareData>) {
        refreshValues(listSquareData)
        openTouchPad(listConstraint[idGridSelected])
    }
}