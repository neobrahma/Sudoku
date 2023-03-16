package com.lconsulting.sudoku.data

import com.lconsulting.sudoku.R

/**
 * data contains information for square
 *
 * @property value value to display on the square, <br/>defaut value = 0
 * @property idTextColor id ressource for text color
 * @property possibility set who contain digits available for this square,</br>default value = (1, 2, 3, 4, 5, 6, 7, 8, 9)
 */
data class SquareData(
    var value: Int = 0,
    var idTextColor: Int = R.color.colorValue,
    val possibility: MutableSet<Int> = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
)