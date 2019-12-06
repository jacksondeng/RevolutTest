package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.Currency
import jacksondeng.revoluttest.model.entity.Rates
import kotlinx.coroutines.launch

class RatesViewModel(private val api: RatesApi) : ViewModel() {

    private var _rates = MutableLiveData<Rates>()
    val rates: LiveData<Rates> = _rates

    fun getRates(base: String = "EUR") {
        viewModelScope.launch {
            val dto = api.getRates(base).await()
            _rates.value = mapToModel(dto)
        }
    }

    fun mapToModel(dto: RatesDTO): Rates {
        return Rates(dto.base, dto.rates.map {
            Currency(name = it.key, rate = it.value)
        })
    }
}