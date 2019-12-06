package jacksondeng.revoluttest.model.dto

import com.squareup.moshi.Json

data class RatesDTO(
    @field:Json(name = "base") val base: String = "ABC",
    @field:Json(name = "date") val date: String = "2019-12-06",
    @field:Json(name = "rates") val rates: Map<String, Double>
)