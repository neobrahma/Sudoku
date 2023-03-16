package com.lconsulting.sudoku.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.RadioGroup
import androidx.annotation.IntDef
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lconsulting.sudoku.MyApplication
import com.lconsulting.sudoku.R
import com.lconsulting.sudoku.data.SquareData
import com.lconsulting.sudoku.data.SudokuData
import com.lconsulting.sudoku.ui.view.SudokuView
import com.lconsulting.sudoku.ui.view.TouchPadViewListener
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

@IntDef(MODE_INSERT, MODE_UPDATE, MODE_LIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class StateMode
const val MODE_INSERT = 0
const val MODE_UPDATE = 1
const val MODE_LIGHT = 2

@IntDef(PLAY, STOP)
@Retention(AnnotationRetention.SOURCE)
annotation class StateResolver
const val PLAY = 0
const val STOP = 1

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: SudokuViewModel

    @Inject
    lateinit var sudokuData: SudokuData

    @StateResolver
    private var statePlayer = PLAY

    private var mIdGrid: Int = -1
    private var mIdSquare: Int = -1
    private var isRepeat: Boolean = false

    private val onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.constraintLayout -> {
                vTouchPad.close()
            }
            R.id.btnPlay -> {
                if (statePlayer == STOP) {
                    setRepeatMode(PLAY, R.drawable.baseline_play_arrow_black_24, false)
                } else {
                    viewModel.startAlgo()
                }
            }
            R.id.btnRepeat -> {
                setRepeatMode(STOP, R.drawable.baseline_stop_black_24, true)
                viewModel.startAlgo()
            }
            R.id.btnPrevious -> {
                tvState.text = resources.getString(R.string.insert_a_value)
                viewModel.setPreviousState()
            }
            R.id.btnNext -> {
                tvState.text = resources.getString(R.string.insert_a_value)
                viewModel.setNextState()
            }
        }
    }

    private val onTouchPadViewListener = object : TouchPadViewListener {
        override fun onSelectIdGrid(idGrid: Int) {
            mIdGrid = idGrid
            viewModel.computeListSquareByIdGrid(idGrid)
        }

        override fun onSelectIdSquare(idSquare: Int) {
            mIdSquare = idSquare
            sudoku.selectSquare(idSquare)
        }

        override fun onSelectValue(value: Int) {
            viewModel.insertValueByUser(value.toString(), mIdGrid, mIdSquare)
        }

        override fun onUnSelectIdGrid(idGrid: Int) {
            sudoku.unSelectGrid(idGrid)
            mIdGrid = -1
        }

        override fun onUnSelectIdSquare(idSquare: Int) {
            sudoku.unSelectSquare(idSquare)
            mIdSquare = -1
        }
    }

    private val onSudokuListener = object : SudokuView.OnSudokuListener {
        override fun onClickSquare(idGrid: Int, idSquare: Int) {

        }
    }

    private val onCheckedChangeColorListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            viewModel.updateColor(
                when (checkedId) {
                    R.id.cb_find -> R.color.colorValueFound
                    R.id.cb_possibility -> R.color.colorValue
                    else -> R.color.colorText

                }
            )
        }

    private val onCheckedChangeModeListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            viewModel.updateMode(
                when (checkedId) {
                    R.id.cb_light_value -> MODE_LIGHT
                    R.id.cb_modify_possibility -> MODE_UPDATE
                    else -> MODE_INSERT

                }
            )

        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as MyApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vTouchPad.setTouchPadViewListener(onTouchPadViewListener)

        llAction.forEach {
            it.setOnClickListener(onClickListener)
        }
        sudoku.setOnSudokuListener(onSudokuListener)
        constraintLayout.setOnClickListener(onClickListener)
        rgColorChoice.setOnCheckedChangeListener(onCheckedChangeColorListener)
        rgModeChoice.setOnCheckedChangeListener(onCheckedChangeModeListener)

        hideActionButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuData = sudokuData
        viewModel.state.observe(this, Observer { updateUI(it) })
    }

    override fun onStart() {
        super.onStart()
//        viewModel.insertValueByUser("0", 0, 0)
//        viewModel.insertValueByUser("4", 0, 1)
//        viewModel.insertValueByUser("6", 0, 2)
//        viewModel.insertValueByUser("0", 0, 3)
//        viewModel.insertValueByUser("8", 0, 4)
//        viewModel.insertValueByUser("1", 0, 5)
//        viewModel.insertValueByUser("7", 0, 6)
//        viewModel.insertValueByUser("5", 0, 7)
//        viewModel.insertValueByUser("2", 0, 8)
//
//        viewModel.insertValueByUser("7", 1, 0)
//        viewModel.insertValueByUser("2", 1, 1)
//        viewModel.insertValueByUser("0", 1, 2)
//        viewModel.insertValueByUser("6", 1, 3)
//        viewModel.insertValueByUser("4", 1, 4)
//        viewModel.insertValueByUser("0", 1, 5)
//        viewModel.insertValueByUser("1", 1, 6)
//        viewModel.insertValueByUser("8", 1, 7)
//        viewModel.insertValueByUser("3", 1, 8)
//
//        viewModel.insertValueByUser("0", 2, 0)
//        viewModel.insertValueByUser("0", 2, 1)
//        viewModel.insertValueByUser("8", 2, 2)
//        viewModel.insertValueByUser("2", 2, 3)
//        viewModel.insertValueByUser("0", 2, 4)
//        viewModel.insertValueByUser("0", 2, 5)
//        viewModel.insertValueByUser("6", 2, 6)
//        viewModel.insertValueByUser("9", 2, 7)
//        viewModel.insertValueByUser("4", 2, 8)
//
//        viewModel.insertValueByUser("8", 3, 0)
//        viewModel.insertValueByUser("9", 3, 1)
//        viewModel.insertValueByUser("7", 3, 2)
//        viewModel.insertValueByUser("5", 3, 3)
//        viewModel.insertValueByUser("0", 3, 4)
//        viewModel.insertValueByUser("4", 3, 5)
//        viewModel.insertValueByUser("2", 3, 6)
//        viewModel.insertValueByUser("0", 3, 7)
//        viewModel.insertValueByUser("3", 3, 8)
//
//        viewModel.insertValueByUser("2", 4, 0)
//        viewModel.insertValueByUser("5", 4, 1)
//        viewModel.insertValueByUser("0", 4, 2)
//        viewModel.insertValueByUser("3", 4, 3)
//        viewModel.insertValueByUser("9", 4, 4)
//        viewModel.insertValueByUser("0", 4, 5)
//        viewModel.insertValueByUser("4", 4, 6)
//        viewModel.insertValueByUser("0", 4, 7)
//        viewModel.insertValueByUser("8", 4, 8)
//
//        viewModel.insertValueByUser("0", 5, 0)
//        viewModel.insertValueByUser("4", 5, 1)
//        viewModel.insertValueByUser("0", 5, 2)
//        viewModel.insertValueByUser("8", 5, 3)
//        viewModel.insertValueByUser("2", 5, 4)
//        viewModel.insertValueByUser("0", 5, 5)
//        viewModel.insertValueByUser("0", 5, 6)
//        viewModel.insertValueByUser("0", 5, 7)
//        viewModel.insertValueByUser("9", 5, 8)
//
//        viewModel.insertValueByUser("0", 6, 0)
//        viewModel.insertValueByUser("7", 6, 1)
//        viewModel.insertValueByUser("9", 6, 2)
//        viewModel.insertValueByUser("4", 6, 3)
//        viewModel.insertValueByUser("3", 6, 4)
//        viewModel.insertValueByUser("5", 6, 5)
//        viewModel.insertValueByUser("0", 6, 6)
//        viewModel.insertValueByUser("2", 6, 7)
//        viewModel.insertValueByUser("8", 6, 8)
//
//        viewModel.insertValueByUser("8", 7, 0)
//        viewModel.insertValueByUser("0", 7, 1)
//        viewModel.insertValueByUser("2", 7, 2)
//        viewModel.insertValueByUser("9", 7, 3)
//        viewModel.insertValueByUser("0", 7, 4)
//        viewModel.insertValueByUser("0", 7, 5)
//        viewModel.insertValueByUser("5", 7, 6)
//        viewModel.insertValueByUser("3", 7, 7)
//        viewModel.insertValueByUser("4", 7, 8)
//
//        viewModel.insertValueByUser("4", 8, 0)
//        viewModel.insertValueByUser("3", 8, 1)
//        viewModel.insertValueByUser("5", 8, 2)
//        viewModel.insertValueByUser("0", 8, 3)
//        viewModel.insertValueByUser("8", 8, 4)
//        viewModel.insertValueByUser("2", 8, 5)
//        viewModel.insertValueByUser("9", 8, 6)
//        viewModel.insertValueByUser("0", 8, 7)
//        viewModel.insertValueByUser("0", 8, 8)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset -> viewModel.reset()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(state: SudokuState) {
        when (state) {
            is SudokuState.FillSquare -> {
                sudoku.updateSudoku(state.sudoku)
                vTouchPad.refreshValues(state.listSquareData)
                tvState.text = resources.getString(state.idRes, state.value)
                displayActionButton(state.isFirstItem, state.isLastItem)
            }
            is SudokuState.ModifyPossibility -> {
                sudoku.updateSudoku(state.sudoku)
            }
            is SudokuState.SuccessAlgo -> {
                val listValueSelected = state.listValueSelected.toList()

                tvState.text = when (state.listValueSelected.size) {
                    2 -> resources.getString(
                        state.idRes,
                        listValueSelected[0],
                        listValueSelected[1]
                    )
                    else -> resources.getString(state.idRes, listValueSelected[0])
                }

                sudoku.selectSquare(
                    state.listSquareSelectedToKeep,
                    listValueSelected,
                    R.color.colorValueFound
                )
                sudoku.selectSquare(
                    state.listSquareSelectedToRemove,
                    listValueSelected,
                    R.color.colorValueRemove
                )
                handler(state.sudoku)
            }
            is SudokuState.Reset -> {
                sudoku.updateSudoku(state.solution)
                hideActionButton()
                tvState.text = resources.getString(R.string.insert_a_value)
            }
            is SudokuState.DisplayMessage -> {
                setRepeatMode(PLAY, R.drawable.baseline_play_arrow_black_24, false)
                tvState.text = resources.getString(state.idResString)
            }
            is SudokuState.RestoreState -> {
                sudoku.updateSudoku(state.sudoku)
                displayActionButton(state.isFirstItem, state.isLastItem)
            }
            is SudokuState.RefreshTouchPad -> {
                sudoku.selectGrid(mIdGrid)
                vTouchPad.open(state.listSquareData)
            }

        }
    }

    private fun handler(sudokayArray: Array<SquareData>) {
        Handler().postDelayed({
            sudoku.updateSudoku(sudokayArray)
            if (isRepeat) {
                viewModel.startAlgo()
            }
        }, 1000)
    }

    private fun setRepeatMode(@StateResolver state: Int, idResDrawable: Int, isRepeat: Boolean) {
        statePlayer = state
        btnPlay.setImageDrawable(resources.getDrawable(idResDrawable))
        this.isRepeat = isRepeat
    }

    private fun hideActionButton() {
        btnPrevious.visibility = View.INVISIBLE
        btnNext.visibility = View.INVISIBLE
    }

    private fun displayActionButton(firstItem: Boolean, lastItem: Boolean) {
        btnPrevious.visibility = if (firstItem) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        btnNext.visibility = if (lastItem) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

}
