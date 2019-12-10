package jacksondeng.revoluttest.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jacksondeng.revoluttest.util.TABLE_NAME_CURRENCY
import java.util.*

@Entity(tableName = TABLE_NAME_CURRENCY)
data class CurrencyModel(
    @PrimaryKey(autoGenerate = true)
    val currency: Currency,
    val rate: Double,
    val imageUrl: String
)