package com.lconsulting.sudoku.data

import android.util.Log
import javax.inject.Inject

class SudokuData @Inject constructor(){

    val SIZE : Int = 81

    var digitsToFind: Int = SIZE
    var sudoku: Array<SquareData> = Array(SIZE) { SquareData() }


    fun reset() {
        digitsToFind = 81
        sudoku = Array(81) { SquareData() }
    }

    operator fun get(position: Int): SquareData {
        return sudoku[position]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SudokuData

        if (digitsToFind != other.digitsToFind) return false
        if (!sudoku.contentEquals(other.sudoku)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = digitsToFind
        result = 31 * result + sudoku.contentHashCode()
        return result
    }

    fun addValue(index: Int, value: Int, idTextColor: Int) {
        sudoku[index].apply {
            this.value = value
            this.idTextColor = idTextColor
        }
        digitsToFind--
    }

    fun removeValue(index: Int, value: Int) {
        sudoku[index].value = 0
        digitsToFind++
    }

    fun updatePossibilty(index: Int, value : Int){
        val possibility = sudoku[index].possibility
        Log.d("tom971", "${sudoku[index].possibility} ${possibility.contains(value)} $value")
        if(possibility.contains(value)){
            val result = possibility.remove(value)
            Log.d("tom971", "renove $result")
        }else{
            val result = possibility.add(value)
            Log.d("tom971", "add $result")
        }
        Log.d("tom971", "${sudoku[index].possibility}")
    }
}