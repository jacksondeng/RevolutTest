package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.State
import kotlinx.coroutines.launch

class RatesViewModel(private val repo: RatesRepository) : ViewModel() {

    private var rates: Rates? = null

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getRates(base: String = "EUR") {
        viewModelScope.launch {
            rates = repo.getRates(base)
            rates?.let {
                _state.value = State.RefreshList(it)
            } ?: run {
                _state.value = State.ShowEmptyScreen("Please try again later")
            }
        }
    }
}