package jacksondeng.revoluttest.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TempTable")
data class TempModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)