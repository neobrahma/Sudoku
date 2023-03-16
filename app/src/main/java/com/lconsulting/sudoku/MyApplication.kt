package com.lconsulting.sudoku

import android.app.Application
import com.lconsulting.sudoku.ui.main.MainFragment
import dagger.Component

@Component
interface ApplicationComponent {
    fun inject(mainFragment: MainFragment)
}

class MyApplication: Application() {
    val appComponent = DaggerApplicationComponent.create()
}