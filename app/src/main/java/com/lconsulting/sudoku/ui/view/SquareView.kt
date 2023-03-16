package com.lconsulting.sudoku.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData

class SquareView : View, ISquareView {

    //0 vide
    //1 valeur
    //2 listes
    var state: Int = 0

    var isClickeable = true

    var text: String = "0"

    private var squareSize: Float = 0.0f
    private var squareSmallSize: Float = 0.0f

    private val square = RectF()
    private val strokeWidth = 2.0f

    private val borderSquarePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = this@SquareView.strokeWidth
    }

    private val numberBigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 1.0f
        textSize = resources.getDimension(R.dimen.numberBigSize)
    }

    private val numberSmallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 1.0f
        textSize = resources.getDimension(R.dimen.numberSmallSize)
    }

    private var setPossibitity: MutableSet<Int>? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        squareSize = w.toFloat()
        squareSmallSize = squareSize / 3

        square.set(
            0.0f + strokeWidth,
            0.0f + strokeWidth,
            squareSize - strokeWidth,
            squareSize - strokeWidth
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(square, borderSquarePaint)
        when (state) {
            1 -> displayBigValue(canvas, text)
            2 -> displayValues(canvas)
        }
    }

    private fun displayValues(canvas: Canvas) {
        for (i in 1..9) {
            if (setPossibitity!!.contains(i)) {
                displaySmallValue(canvas, i.toString(), (i - 1) % 3, (i - 1) / 3)
            }
        }
    }

    private fun displayBigValue(canvas: Canvas, value: String) {
        numberBigPaint.apply {
            color = if (isClickeable) {
                Color.BLACK
            } else {
                Color.GRAY
            }
        }
        canvas.apply {
            val x = (squareSize - numberBigPaint.measureText(value)) / 2
            val textSize = resources.getDimensionPixelSize(R.dimen.numberBigSize).toFloat()
            val delta = (squareSize - textSize)
            val y: Float = (squareSize + textSize - delta) / 2
            drawText(value, x, y, numberBigPaint)
        }
    }

    private fun displaySmallValue(canvas: Canvas, value: String, positionX: Int, positionY: Int) {
        canvas.apply {
            val x =
                (squareSmallSize * positionX) + ((squareSmallSize - numberSmallPaint.measureText(
                    value
                )) / 2)
            val textSize = resources.getDimensionPixelSize(R.dimen.numberSmallSize).toFloat()
            val y = (squareSmallSize * positionY) + textSize
            drawText(value, x, y, numberSmallPaint)
        }
    }

    fun displaySquareEmpty() {
        state = 0
        invalidate()
    }

    fun displaySquareWithValues() {
        state = 2
        invalidate()
    }

    fun displayValue(txt: String) {
        state = 1
        text = txt
        invalidate()
    }

    fun displayValue(txt: String, isGrey: Boolean) {
        state = 1
        text = txt
        isClickeable = isGrey
        invalidate()
    }

    fun displayValue(data: SquareData) {
        if (data.value != 0) {
            state = 1
            text = data.value.toString()
        } else {
            state = 2
            setPossibitity = data.possibility
        }
        invalidate()
    }

    override fun updateSquare(square: SquareData) {
        Log.d("tom971", "updateSquare")
    }

    override fun unSelectSquare() {
        Log.d("tom971", "unSelectSquare")
    }

    override fun selectSquare() {
        Log.d("tom971", "selectSquare")
    }

    override fun selectSquare(listValueSelected: List<Int>, idResColor: Int) {
        Log.d("tom971", "selectSquare")
    }

    override fun enlightenedValue(value: Int) {
        Log.d("tom971", "enlightenedValue")
    }

    override fun unEnlightenedValue() {
        Log.d("tom971", "unEnlightenedValue")
    }

}