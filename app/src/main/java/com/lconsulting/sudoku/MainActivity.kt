package com.lconsulting.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lconsulting.sudoku.ui.main.MainFragment
import kotlinx.android.synthetic.main.main_activity_1.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

    }
}
