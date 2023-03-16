package com.lconsulting.sudoku.ui.view

import com.lconsulting.sudoku.data.SquareData

interface ISquareView {
    fun updateSquare(square: SquareData)
    fun unSelectSquare()
    fun selectSquare()
    fun selectSquare(listValueSelected: List<Int>, idResColor : Int)
    fun enlightenedValue(value: Int)
    fun unEnlightenedValue()
}