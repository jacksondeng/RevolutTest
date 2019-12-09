package jacksondeng.revoluttest.util

data class EditTextFlow(
    val query: String,
    val type: Type
) {
    enum class Type { BEFORE, AFTER, ON }
}
