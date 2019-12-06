package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.model.dto.RatesDTO
import kotlinx.coroutines.launch

class RatesViewModel(private val api: RatesApi) : ViewModel() {

    private var _rates = MutableLiveData<RatesDTO>()
    val rates : LiveData<RatesDTO> = _rates

    fun getRates(base: String = "EUR") {
        viewModelScope.launch {
            _rates.value = api.getRates(base).await()
        }
    }
}