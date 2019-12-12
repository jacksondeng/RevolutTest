package jacksondeng.revoluttest.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.util.ViewModelFactory
import jacksondeng.revoluttest.util.getSelectedBase
import jacksondeng.revoluttest.view.adapter.InterActionListener
import jacksondeng.revoluttest.view.adapter.RatesAdapter
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), InterActionListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: RatesViewModel

    @Inject
    lateinit var sharePref: SharedPreferences

    private lateinit var ratesAdapter: RatesAdapter

    private val compositeDisposable = CompositeDisposable()

    override fun getInputStream(flow: Flowable<String>) {
        compositeDisposable.add(
            flow.flatMap {
                val multiplier = if (it.isNotEmpty()) {
                    it.toDouble()
                } else {
                    1.0
                }
                viewModel.pausePolling()
                viewModel.calculateRate(multiplier)
                Flowable.just(multiplier)
            }
                .subscribe({
                    viewModel.pollRates(
                        base = sharePref.getSelectedBase(),
                        multiplier = it
                    )
                }, {

                })
        )
    }

    override fun onItemClicked(position: Int) {
        super.onItemClicked(position)
        viewModel.pausePolling()
        viewModel.pollRates()
        ratesAdapter.moveItemToTop(position)
        ratesRv.smoothScrollToPosition(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initViews()
        initVm()
    }

    override fun onResume() {
        super.onResume()
        viewModel.pollRates(sharePref.getSelectedBase())
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePolling()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun initViews() {
        initAdapters()
        initRv(this@MainActivity)
    }

    private fun initAdapters() {
        ratesAdapter = RatesAdapter(this@MainActivity, sharePref)
    }

    private fun initRv(context: Context) {
        ratesRv.apply {
            adapter = ratesAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            // Prevent imageview flickering when submitList is called
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun initVm() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[RatesViewModel::class.java]
        viewModel.getCachedRates(base = sharePref.getSelectedBase(), multiplier = 1.0)
        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is State.RefreshList -> {
                    ratesAdapter.submitList(state.rates)
                }

                is State.ShowEmptyScreen -> {
                    // TODO: Show empty layout
                }
            }
        })
    }
}
