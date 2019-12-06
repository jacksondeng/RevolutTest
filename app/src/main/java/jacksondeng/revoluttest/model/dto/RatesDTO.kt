package jacksondeng.revoluttest.model.dto

import com.squareup.moshi.Json
import org.json.JSONObject

data class RatesDTO(
    @field:Json(name = "base") val base: String = "EUR",
    @field:Json(name = "date") val date: String = "2019-12-06"
    //@field:Json(name = "rates") val rates: JSONObject
)