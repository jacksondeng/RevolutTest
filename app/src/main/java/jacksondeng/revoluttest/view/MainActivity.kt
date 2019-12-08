package jacksondeng.revoluttest.view

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.view.adapter.RatesAdapter
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: RatesViewModel

    lateinit var ratesAdapter: RatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initViews()
        initVm()
    }

    override fun onResume() {
        super.onResume()
        viewModel.pollRates("EUR")
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }

    private fun initViews() {
        initAdapters()
        initRv(this@MainActivity)
    }

    private fun initAdapters() {
        ratesAdapter = RatesAdapter()
    }

    private fun initRv(context: Context) {
        ratesRv.apply {
            adapter = ratesAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun initVm() {
        viewModel.viewState.observe(this, Observer { state ->
            when (state) {
                is State.RefreshList -> {
                    ratesAdapter.submitList(state.rates.rates)
                }

                is State.ShowEmptyScreen -> {
                    // TODO: Show empty layout
                }
            }
        })
    }
}
