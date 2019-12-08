package jacksondeng.revoluttest.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject
import jacksondeng.revoluttest.view.adapter.RatesAdapter

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: RatesViewModel

    lateinit var ratesAdapter: RatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        ratesAdapter = RatesAdapter()
        ratesRv.adapter = ratesAdapter

        fab.setOnClickListener {
            viewModel.getRates("EUR")
            viewModel.pollRates("EUR")
        }

        /*viewModel.state.observe(this, Observer { state ->
            when (state) {
                is State.RefreshList -> {
                    ratesAdapter.submitList(state.rates.rates)
                }

                is State.ShowEmptyScreen -> {
                    // TODO: Show empty layout
                }
            }
        })*/

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

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
