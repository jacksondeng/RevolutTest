package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.Rates
import kotlinx.coroutines.launch

class RatesViewModel(private val repo: RatesRepository) : ViewModel() {

    private var _rates = MutableLiveData<Rates>()
    val rates: LiveData<Rates> = _rates

    fun getRates(base: String = "EUR") {
        viewModelScope.launch {
            val rates = repo.getRates(base)
            rates?.let {
                _rates.value = it
            } ?: run {
                // API failed and cache is empty
                // TODO : Show empty layout or error message
            }
        }
    }
}