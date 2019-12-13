package jacksondeng.revoluttest.util

import jacksondeng.revoluttest.model.entity.CurrencyModel

sealed class State {
    data class Loaded(val rates: List<CurrencyModel>) : State()
    data class Calculated(val rates: List<CurrencyModel>) : State()
    data class Loading(val message: String = "") : State()
    data class Error(val message: String = "") : State()
    data class RefreshList(val rates:List<CurrencyModel>) : State()
}