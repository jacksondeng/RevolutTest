package jacksondeng.revoluttest.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.util.*
import jacksondeng.revoluttest.view.adapter.RatesAdapter
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: RatesViewModel

    @Inject
    lateinit var sharePref: SharedPreferences

    private lateinit var ratesAdapter: RatesAdapter

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initViews()
        initVm()
    }

    override fun onResume() {
        super.onResume()
        window?.decorView?.clearFocus()
        viewModel.pollRates()
    }

    override fun onPause() {
        super.onPause()
        this.hideSoftKeyboard()
        viewModel.pausePolling()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun initViews() {
        initAdapters()
        initRv(this@MainActivity)
        RxView.clicks(emptyLayout)
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .map { viewModel.retry() }
            .subscribe()
    }

    private fun initAdapters() {
        ratesAdapter = RatesAdapter(sharePref)
        compositeDisposable.add(ratesAdapter.clickSubject.subscribe {
            viewModel.pausePolling()
            viewModel.getCachedRates(multiplier = it.rate, baseChanged = true)
        })

        compositeDisposable.add(ratesAdapter.textChangeSubject.subscribe {
            viewModel.getCachedRates(multiplier = it)
        })

        compositeDisposable.add(ratesAdapter.focusChangesSubject.subscribe { hasFocus ->
            if (hasFocus) {
                viewModel.pausePolling()
            } else {
                viewModel.pollRates()
            }
        })
    }

    private fun initRv(context: Context) {
        ratesRv.apply {
            adapter = ratesAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

            // Prevent imageview flickering when submitList is called
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            setOnTouchListener { v, _ ->
                v.hideKeyBoard()
                false
            }
        }
    }

    private fun initVm() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[RatesViewModel::class.java]
        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is State.Loaded -> {
                    showList(state.rates)
                }

                is State.Calculated -> {
                    viewModel.pausePolling()
                    showList(state.rates)
                }

                is State.Loading -> {
                    viewModel.pollRates()
                    ratesRv.gone()
                    loader.showAndPlay()
                    emptyLayout.gone()
                }

                is State.RefreshList -> {
                    showList(state.rates)
                    ratesRv.post {
                        ratesRv.smoothScrollToPosition(0)
                        viewModel.pausePolling()
                        viewModel.pollRates()
                    }
                }

                is State.Error -> {
                    emptyLayout.visible()
                    ratesRv.gone()
                    loader.gone()
                }
            }
        })
    }

    private fun showList(rates: List<CurrencyModel>) {
        ratesAdapter.submitList(rates)
        ratesRv.visible()
        loader.stopAndHide()
        emptyLayout.gone()
    }
}
