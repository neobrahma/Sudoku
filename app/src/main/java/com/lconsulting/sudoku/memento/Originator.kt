package com.lconsulting.sudoku.memento

class Originator {
    private var idGrid: Int = -1
    private var idSquare: Int = -1
    private var value: Int = -1
    private var idResColor = -1

    fun setState(idG: Int, idS: Int, v: Int, idRes : Int) {
        idGrid = idG
        idSquare = idS
        value = v
        idResColor = idRes
    }

    fun save(): Memento {
        return Memento(idGrid, idSquare, value, idResColor)
    }

    fun clearState() {
        idGrid = -1
        idSquare = -1
        value = -1
    }

}