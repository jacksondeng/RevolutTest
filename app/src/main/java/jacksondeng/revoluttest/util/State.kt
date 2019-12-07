package jacksondeng.revoluttest.util

import jacksondeng.revoluttest.model.entity.Rates

sealed class State {
    data class RefreshList(val rates: Rates) : State()
    data class ShowEmptyScreen(val message: String) : State()
}