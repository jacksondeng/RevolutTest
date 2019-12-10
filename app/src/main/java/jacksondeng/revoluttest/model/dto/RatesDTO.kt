package jacksondeng.revoluttest.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import jacksondeng.revoluttest.util.TABLE_NAME_RATES

@Entity(tableName = TABLE_NAME_RATES)
data class RatesDTO(
    @PrimaryKey
    @field:Json(name = "base") val base: String = "ABC",
    @field:Json(name = "date") val date: String = "2019-12-06",
    @field:Json(name = "rates") val rates: Map<String, Double>
)