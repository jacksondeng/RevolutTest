package jacksondeng.revoluttest.util

import jacksondeng.revoluttest.model.entity.CurrencyModel

sealed class State {
    data class RefreshList(val rates: List<CurrencyModel>) : State()
    data class ShowEmptyScreen(val message: String = "") : State()
}