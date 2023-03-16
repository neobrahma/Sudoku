package com.lconsulting.sudoku.memento

data class Memento(var idGrid : Int, var idSquare : Int, var value : Int, var idResColor : Int)